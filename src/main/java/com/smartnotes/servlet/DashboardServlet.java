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
 * Handles the main dashboard view — lists all notes.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());
    private final NoteDAO noteDAO = new NoteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            List<Note> notes = noteDAO.getAllNotes();
            request.setAttribute("notes", notes);
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading dashboard", e);
            request.setAttribute("error", "Unable to load notes. Please try again later.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
}
