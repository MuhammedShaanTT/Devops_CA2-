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
 * REST-style endpoint for generating study notes via the Gemini API.
 * Expects JSON body: {"topic": "..."}
 * Returns JSON:      {"result": "..."}
 */
@WebServlet(name = "GenerateStudyNotesServlet", urlPatterns = {"/generate-study-notes"})
public class GenerateStudyNotesServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(GenerateStudyNotesServlet.class.getName());
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
            String topic = body.has("topic") ? body.get("topic").getAsString() : "";

            String result = geminiClient.generateStudyNotes(topic);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("result", result);

            try (PrintWriter out = response.getWriter()) {
                out.print(GSON.toJson(jsonResponse));
                out.flush();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in generate-study-notes endpoint", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("result", "Error: Study notes generation failed. Please try again.");

            try (PrintWriter out = response.getWriter()) {
                out.print(GSON.toJson(errorResponse));
                out.flush();
            }
        }
    }
}
