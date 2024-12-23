package com.noteapp.controller;

import com.noteapp.note.model.Note;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * Một Controller cho việc hiển thị các note được truy cập gần nhất
 * lên trang dashboard của User
 * @author Nhóm 17
 * @see DashboardController
 */
public class RecentlyNoteCardController extends Controller {
    @FXML
    private Text header;
    @FXML
    private Text lastModifiedDate;
    @FXML
    private Text editor;
    
    private Note note;
    
    public void setNote(Note note) {
        this.note = note;
        header.setText(note.getHeader());
        lastModifiedDate.setText(note.getLastModifiedDate().toString());
        editor.setText(note.getAuthor());
    }
    
    public Note getNote() {
        return note;
    }
}