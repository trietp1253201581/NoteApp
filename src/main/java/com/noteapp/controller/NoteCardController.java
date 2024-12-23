package com.noteapp.controller;

import com.noteapp.note.model.Note;
import com.noteapp.note.model.NoteFilter;
import com.noteapp.note.model.ShareNote;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * FXML Controller class cho các Note Card hiển thị trên trang dashboard
 * @author Nhóm 17
 * @see DashboardController
 */
public class NoteCardController extends Controller {  
    //Các thuộc tính FXML
    @FXML
    private Label header;
    @FXML
    private Label lastModifiedDate;
    @FXML
    private Label author;
    @FXML
    private Label filters;
    @FXML
    private Label shareStatus;
    
    private int id;
    private String editor;

    public String getHeader() {
        return header.getText();
    }

    public void setHeader(String header) {
        this.header.setText(header);
    }

    public String getLastModifiedDate() {
        return lastModifiedDate.getText();
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate.setText(lastModifiedDate);
    }

    public String getAuthor() {
        return author.getText();
    }

    public void setAuthor(String author) {
        this.author.setText(author);
    }

    public String getShareStatus() {
        return shareStatus.getText();
    }

    public void setShareStatus(String shareStatus) {
        this.shareStatus.setText(shareStatus);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }
    
    /**
     * Thiết lập dữ liệu cho Note Card
     * @param note note chứa dữ liệu cần chuyển vào Note Card
     */
    public void setData(Note note) {
        this.id = note.getId();
        this.editor = note.getAuthor();
        header.setText(note.getHeader());
        lastModifiedDate.setText(String.valueOf(note.getLastModifiedDate()));
        author.setText(note.getAuthor());
        String filtersString = "";
        for(NoteFilter filter: note.getFilters()) {
            filtersString += filter.getFilter() + ", ";
        }
        if(filtersString.isEmpty()) {
            filters.setText(filtersString);
        } else {
            filters.setText(filtersString.substring(0, filtersString.length() - 2));
        }
        if(!note.isPubliced()) {
            shareStatus.setText("PRIVATE");
        } else {
            shareStatus.setText("PUBLIC");
        }
    }
    
    /**
     * Thiết lập dữ liệu cho Note Card
     * @param shareNote shareNote chứa dữ liệu cần chuyển
     */
    public void setData(ShareNote shareNote) {
        this.setData((Note) shareNote);
        this.editor = shareNote.getEditor();
        shareStatus.setText(shareNote.getShareType().toString());
    }
}