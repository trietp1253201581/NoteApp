package com.noteapp.note.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Một transfer cho dữ liệu của các note
 * @author Nhóm 17
 * @version 1.0
 */
public class Note {
    private int id;
    private String author;
    private String header;
    private List<NoteBlock> blocks;
    private Date lastModifiedDate;
    private List<NoteFilter> filters;
    private boolean publiced;

    public Note() {
        this.id = -1;
        this.author = "";
        this.header = "";
        this.blocks = new ArrayList<>();
        this.lastModifiedDate = Date.valueOf(LocalDate.MIN);
        this.filters = new ArrayList<>();
        this.publiced = false;
    }

    public Note(String author, String header) {
        this.id = -1;
        this.author = author;
        this.header = header;
        this.blocks = new ArrayList<>();
        this.lastModifiedDate = Date.valueOf(LocalDate.MIN);
        this.filters = new ArrayList<>();
        this.publiced = false;
    }

    public Note(int id, String author, String header, List<NoteBlock> blocks, Date lastModifiedDate, List<NoteFilter> filters) {
        this.id = id;
        this.author = author;
        this.header = header;
        this.blocks = blocks;
        this.lastModifiedDate = lastModifiedDate;
        this.filters = filters;
        this.publiced = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public List<NoteBlock> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<NoteBlock> blocks) {
        this.blocks = blocks;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public List<NoteFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<NoteFilter> filters) {
        this.filters = filters;
    }

    public boolean isPubliced() {
        return publiced;
    }

    public void setPubliced(boolean publiced) {
        this.publiced = publiced;
    }
    
    /**
     * Kiểm tra một {@link Note} có mang giá trị mặc định không
     * @return {@code true} nếu id bằng -1, ngược lại là {@code false}
     */
    public boolean isDefaultValue() {
        return id == -1;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.id;
        return hash;
    }
    
    /**
     * So sánh một {@link Object} với {@link Note} này.
     * @param obj Object cần so sánh
     * @return {@code true} nếu obj là một thể hiện của Note
     * và thuộc tính {@code noteId} của obj bằng với
     * {@code noteId} của Note, {@code false} nếu ngược lại.
     * @see hashCode()
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Note)) {
            return false;
        }
        final Note other = (Note) obj;
        return this.id == other.id;
    }

    @Override
    public String toString() {
        return "Note{" + 
                "id=" + id + 
                ", author=" + author + 
                ", header=" + header + 
                ", blocks=" + blocks + 
                ", lastModifiedDate=" + lastModifiedDate + 
                ", filters=" + filters + 
                ", publiced=" + publiced +
                '}';
    }
}