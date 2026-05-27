package com.smartnotes.model;

import java.sql.Timestamp;

/**
 * Note model representing a single note in the SmartNotes application.
 */
public class Note {

    private int id;
    private String title;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    /** Default no-arg constructor. */
    public Note() {
    }

    /**
     * Constructor for creating a new note (without id or timestamps).
     *
     * @param title   the note title
     * @param content the note content
     */
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    /**
     * Full constructor with all fields.
     *
     * @param id        the note id
     * @param title     the note title
     * @param content   the note content
     * @param createdAt the creation timestamp
     * @param updatedAt the last-updated timestamp
     */
    public Note(int id, String title, String content, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ── Getters & Setters ───────────────────────────────────────────────

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + (content != null && content.length() > 50
                        ? content.substring(0, 50) + "..." : content) + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
