package com.smartnotes.dao;

import com.smartnotes.model.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the {@link Note} entity.
 * All queries use {@link PreparedStatement} and try-with-resources for
 * proper resource cleanup.
 */
public class NoteDAO {

    private static final Logger LOGGER = Logger.getLogger(NoteDAO.class.getName());

    /**
     * Retrieves every note, ordered by most-recently updated first.
     *
     * @return list of all notes
     */
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT id, title, content, created_at, updated_at FROM notes ORDER BY updated_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                notes.add(mapRow(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all notes", e);
        }
        return notes;
    }

    /**
     * Retrieves a single note by its primary key.
     *
     * @param id the note id
     * @return the matching {@link Note}, or {@code null} if not found
     */
    public Note getNoteById(int id) {
        String sql = "SELECT id, title, content, created_at, updated_at FROM notes WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching note by id: " + id, e);
        }
        return null;
    }

    /**
     * Inserts a new note into the database.
     *
     * @param note the note to add (id is ignored / auto-generated)
     */
    public void addNote(Note note) {
        String sql = "INSERT INTO notes (title, content) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, note.getTitle());
            ps.setString(2, note.getContent());
            ps.executeUpdate();
            LOGGER.info("Note added successfully: " + note.getTitle());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error adding note", e);
        }
    }

    /**
     * Updates an existing note's title and content.
     *
     * @param note the note to update (must have a valid id)
     */
    public void updateNote(Note note) {
        String sql = "UPDATE notes SET title = ?, content = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, note.getTitle());
            ps.setString(2, note.getContent());
            ps.setInt(3, note.getId());
            ps.executeUpdate();
            LOGGER.info("Note updated successfully: id=" + note.getId());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating note id: " + note.getId(), e);
        }
    }

    /**
     * Deletes a note by its primary key.
     *
     * @param id the id of the note to delete
     */
    public void deleteNote(int id) {
        String sql = "DELETE FROM notes WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
            LOGGER.info("Note deleted successfully: id=" + id);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting note id: " + id, e);
        }
    }

    /**
     * Searches notes whose title or content contains the given keyword
     * (case-insensitive), ordered by most-recently updated first.
     *
     * @param keyword the search term
     * @return list of matching notes
     */
    public List<Note> searchNotes(String keyword) {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT id, title, content, created_at, updated_at FROM notes " +
                     "WHERE title LIKE ? OR content LIKE ? ORDER BY updated_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notes.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching notes with keyword: " + keyword, e);
        }
        return notes;
    }

    // ── Helper ──────────────────────────────────────────────────────────

    /**
     * Maps the current row of a {@link ResultSet} to a {@link Note} instance.
     */
    private Note mapRow(ResultSet rs) throws SQLException {
        return new Note(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("updated_at")
        );
    }
}
