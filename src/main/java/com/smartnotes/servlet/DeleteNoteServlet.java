package com.smartnotes.servlet;

import com.smartnotes.dao.NoteDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles note deletion.
 * Supports both GET and POST for maximum compatibility.
 */
@WebServlet(name = "DeleteNoteServlet", urlPatterns = {"/delete"})
public class DeleteNoteServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(DeleteNoteServlet.class.getName());
    private final NoteDAO noteDAO = new NoteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processDelete(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processDelete(request, response);
    }

    /**
     * Common delete logic shared by doGet and doPost.
     */
    private void processDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            noteDAO.deleteNote(id);
            LOGGER.info("Note deleted: id=" + id);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid note id parameter on delete", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error deleting note", e);
        }

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
