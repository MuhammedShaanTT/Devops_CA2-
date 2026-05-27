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
 * Handles adding a new note.
 * GET  → displays the add-note form.
 * POST → persists the new note and redirects to the dashboard.
 */
@WebServlet(name = "AddNoteServlet", urlPatterns = {"/add"})
public class AddNoteServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(AddNoteServlet.class.getName());
    private final NoteDAO noteDAO = new NoteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            List<Note> notes = noteDAO.getAllNotes();
            request.setAttribute("notes", notes);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load notes for sidebar", e);
        }
        request.getRequestDispatcher("/add-note.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            Note note = new Note(title, content);
            noteDAO.addNote(note);

            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error adding note", e);
            request.setAttribute("error", "Failed to add note. Please try again.");
            request.getRequestDispatcher("/add-note.jsp").forward(request, response);
        }
    }
}
