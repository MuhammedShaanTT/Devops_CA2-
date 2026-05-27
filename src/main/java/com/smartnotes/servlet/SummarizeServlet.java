package com.smartnotes.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.smartnotes.util.GeminiAPIClient;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST-style endpoint for summarising text via the Gemini API.
 * Expects JSON body: {"text": "..."}
 * Returns JSON:      {"result": "..."}
 */
@WebServlet(name = "SummarizeServlet", urlPatterns = {"/summarize"})
public class SummarizeServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SummarizeServlet.class.getName());
    private static final Gson GSON = new Gson();
    private final GeminiAPIClient geminiClient = new GeminiAPIClient();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try {
            // Read JSON body
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }

            JsonObject body = JsonParser.parseString(sb.toString()).getAsJsonObject();
            String text = body.has("text") ? body.get("text").getAsString() : "";

            String result = geminiClient.summarize(text);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("result", result);

            try (PrintWriter out = response.getWriter()) {
                out.print(GSON.toJson(jsonResponse));
                out.flush();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in summarize endpoint", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("result", "Error: Summarization failed. Please try again.");

            try (PrintWriter out = response.getWriter()) {
                out.print(GSON.toJson(errorResponse));
                out.flush();
            }
        }
    }
}
