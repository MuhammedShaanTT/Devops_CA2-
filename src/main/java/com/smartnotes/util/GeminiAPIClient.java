package com.smartnotes.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Client for the Google Gemini generative-AI API.
 * Uses OkHttp for HTTP communication and Gson for JSON handling.
 */
public class GeminiAPIClient {

    private static final Logger LOGGER = Logger.getLogger(GeminiAPIClient.class.getName());
    private static final Gson GSON = new Gson();
    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

    private static final String API_KEY = System.getenv("GEMINI_API_KEY");
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    // Redis Configuration
    private static final String REDIS_HOST = System.getenv("REDIS_HOST") != null ? System.getenv("REDIS_HOST") : "localhost";
    private static final JedisPool JEDIS_POOL = new JedisPool(new JedisPoolConfig(), REDIS_HOST, 6379);

    /**
     * Summarises the given text into key bullet points.
     *
     * @param text the text to summarise
     * @return bullet-point summary or an error message
     */
    public String summarize(String text) {
        String prompt = "You are a helpful AI assistant. Summarize the following note into a well-organized, easy-to-read summary. Use clear markdown formatting (like bold text and bullet points) and start with a brief, friendly introductory sentence.\n\nNote Content:\n" + text;
        return callGemini(prompt);
    }

    /**
     * Translates the given text into the specified language.
     *
     * @param text           the text to translate
     * @param targetLanguage the target language name (e.g. "Spanish")
     * @return translated text or an error message
     */
    public String translate(String text, String targetLanguage) throws Exception {
        String prompt = "You are a helpful AI assistant. Translate the following text to " + targetLanguage + ". Provide the translation using clear markdown formatting, and start with a brief, friendly introductory sentence indicating the target language.\n\nText:\n" + text;
        return callGemini(prompt);
    }

    /**
     * Generates comprehensive study notes on the given topic.
     *
     * @param topic the study topic
     * @return generated study notes or an error message
     */
    public String generateStudyNotes(String topic) throws Exception {
        String prompt = "You are an expert tutor. Generate comprehensive, well-structured study notes on the following topic: " + topic + ".\n\nPlease organize the response using beautiful markdown formatting (headers, bullet points, bold text) and include:\n1. A friendly introductory sentence\n2. A Brief Summary\n3. Key Points\n4. Important Definitions\n5. 3 Sample Questions for practice";
        return callGemini(prompt);
    }

    /**
     * Generates flashcards based on the provided text.
     * Instructs the AI to return ONLY a JSON array of objects with "question" and "answer" keys.
     * 
     * @param text the note text to turn into flashcards
     * @return JSON string of flashcards or an error message
     */
    public String generateFlashcards(String text) {
        String prompt = "You are an expert tutor. Create a comprehensive set of flashcards covering all the key concepts, definitions, and topics in the following text. \n\n" +
                "CRITICAL INSTRUCTION: You MUST return the result as a raw JSON array of objects. Do not include any markdown formatting (like ```json), do not include any introductory or concluding text. ONLY output the raw JSON array.\n\n" +
                "Format requirement:\n" +
                "[\n" +
                "  {\"question\": \"What is...\", \"answer\": \"It is...\"},\n" +
                "  {\"question\": \"...\", \"answer\": \"...\"}\n" +
                "]\n\n" +
                "Text:\n" + text;
        String result = callGemini(prompt);
        
        // Sometimes the AI still wraps the output in markdown blocks despite instructions.
        // We must strip them so the frontend can parse the raw JSON.
        if (result != null && !result.startsWith("Error:")) {
            result = result.trim();
            if (result.startsWith("```json")) {
                result = result.substring(7);
            } else if (result.startsWith("```")) {
                result = result.substring(3);
            }
            if (result.endsWith("```")) {
                result = result.substring(0, result.length() - 3);
            }
            result = result.trim();
        }
        return result;
    }

    // ── Internal ────────────────────────────────────────────────────────

    /**
     * Sends a prompt to the Gemini API and returns the generated text.
     *
     * @param prompt the user prompt
     * @return the model's text response, or a human-readable error string
     */
    private String callGemini(String prompt) {
        if (API_KEY == null || API_KEY.isBlank()) {
            LOGGER.warning("GEMINI_API_KEY environment variable is not set");
            return "Error: GEMINI_API_KEY environment variable is not set. Please configure it to use AI features.";
        }

        String cacheKey = generateCacheKey(prompt);
        
        // 1. Check Redis Cache First
        try (Jedis jedis = JEDIS_POOL.getResource()) {
            String cachedResponse = jedis.get(cacheKey);
            if (cachedResponse != null) {
                LOGGER.info("Cache hit! Returning summary from Redis.");
                return cachedResponse;
            }
        } catch (Exception e) {
            LOGGER.warning("Redis cache error (read): " + e.getMessage());
        }

        try {
            // Build the request body
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", prompt);

            JsonArray partsArray = new JsonArray();
            partsArray.add(textPart);

            JsonObject content = new JsonObject();
            content.add("parts", partsArray);

            JsonArray contentsArray = new JsonArray();
            contentsArray.add(content);

            JsonObject requestBody = new JsonObject();
            requestBody.add("contents", contentsArray);

            Request request = new Request.Builder()
                    .url(API_URL + API_KEY)
                    .post(RequestBody.create(requestBody.toString(), JSON_MEDIA))
                    .build();

            try (Response response = HTTP_CLIENT.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    LOGGER.log(Level.WARNING, "Gemini API error (HTTP " + response.code() + "): " + errorBody);
                    return "Error: Gemini API returned HTTP " + response.code() + ". Please try again later.";
                }

                String responseBody = response.body() != null ? response.body().string() : "";
                String extractedText = extractText(responseBody);
                
                // 2. Save successful response to Redis Cache (expire in 24 hours)
                if (!extractedText.startsWith("Error:")) {
                    try (Jedis jedis = JEDIS_POOL.getResource()) {
                        jedis.setex(cacheKey, 86400, extractedText);
                        LOGGER.info("Saved AI response to Redis Cache.");
                    } catch (Exception e) {
                        LOGGER.warning("Redis cache error (write): " + e.getMessage());
                    }
                }
                
                return extractedText;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error calling Gemini API", e);
            return "Error: Unable to connect to Gemini API. Please check your network connection and try again.";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error calling Gemini API", e);
            return "Error: An unexpected error occurred while processing your request.";
        }
    }

    /**
     * Extracts the generated text from the Gemini API JSON response.
     * Path: candidates[0].content.parts[0].text
     */
    private String extractText(String responseBody) {
        try {
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray candidates = json.getAsJsonArray("candidates");
            if (candidates != null && candidates.size() > 0) {
                JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
                JsonObject contentObj = firstCandidate.getAsJsonObject("content");
                JsonArray parts = contentObj.getAsJsonArray("parts");
                if (parts != null && parts.size() > 0) {
                    return parts.get(0).getAsJsonObject().get("text").getAsString();
                }
            }
            LOGGER.warning("Unexpected Gemini response structure: " + responseBody);
            return "Error: Could not parse AI response.";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error parsing Gemini response: " + responseBody, e);
            return "Error: Could not parse AI response.";
        }
    }

    /**
     * Generates a SHA-256 hash of the prompt to use as a Redis key.
     */
    private String generateCacheKey(String prompt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(prompt.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return "gemini_cache:" + hexString.toString();
        } catch (Exception e) {
            return "gemini_cache:" + prompt.hashCode();
        }
    }
}
