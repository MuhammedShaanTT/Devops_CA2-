package com.smartnotes.servlet;

import com.smartnotes.dao.NoteDAO;
import com.smartnotes.model.Note;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles note searching by keyword.
 */
@WebServlet(name = "SearchNoteServlet", urlPatterns = {"/search"})
public class SearchNoteServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SearchNoteServlet.class.getName());
    private final NoteDAO noteDAO = new NoteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            String query = request.getParameter("query");

            if (query != null && !query.isBlank()) {
                List<Note> notes = noteDAO.searchNotes(query.trim());
                request.setAttribute("notes", notes);
                request.setAttribute("searchQuery", query);
            } else {
                List<Note> notes = noteDAO.getAllNotes();
                request.setAttribute("notes", notes);
            }

            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error searching notes", e);
            request.setAttribute("error", "Search failed. Please try again.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}
