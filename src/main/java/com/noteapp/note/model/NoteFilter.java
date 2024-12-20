package com.noteapp.note.model;

import java.util.Objects;

/**
 * Một transfer cho dữ liệu của các filter của 1 note
 * @author Nhóm 17
 * @version 1.0
 */
public class NoteFilter {
    private String filter;

    public NoteFilter() {
        this.filter = "";
    }

    public NoteFilter(String filter) {
        this.filter = filter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * Kiểm tra một {@link NoteFilter} có mang giá trị mặc định không
     * @return {@code true} nếu filter là một chuỗi rỗng, ngược lại là {@code false}
     */
    public boolean isDefaultValue() {
        return "".equals(this.filter);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.filter);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NoteFilter other = (NoteFilter) obj;
        return Objects.equals(this.filter, other.filter);
    }

    @Override
    public String toString() {
        return "NoteFilter{" + "filte=" + filter + '}';
    }

}
