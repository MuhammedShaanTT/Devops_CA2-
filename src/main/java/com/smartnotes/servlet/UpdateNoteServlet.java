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
 * Handles updating an existing note.
 * GET  → loads the note and forwards to edit-note.jsp.
 * POST → saves the updated note and redirects to the dashboard.
 */
@WebServlet(name = "UpdateNoteServlet", urlPatterns = {"/update"})
public class UpdateNoteServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(UpdateNoteServlet.class.getName());
    private final NoteDAO noteDAO = new NoteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Note note = noteDAO.getNoteById(id);

            if (note != null) {
                List<Note> notes = noteDAO.getAllNotes();
                request.setAttribute("note", note);
                request.setAttribute("notes", notes);
                request.getRequestDispatcher("/edit-note.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/dashboard");
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid note id parameter", e);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading note for editing", e);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            Note note = new Note();
            note.setId(id);
            note.setTitle(title);
            note.setContent(content);

            noteDAO.updateNote(note);

            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid note id parameter on update", e);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error updating note", e);
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}
