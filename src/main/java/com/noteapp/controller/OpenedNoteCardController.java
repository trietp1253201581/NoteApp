package com.noteapp.controller;

import com.noteapp.note.model.Note;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 *
 * @author admin
 */
public class OpenedNoteCardController extends Controller {
    @FXML
    private Label header;
    @FXML
    private ImageView removeNote;
    
    private Note note;

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public String getHeader() {
        return header.getText();
    }

    public ImageView getRemoveNote() {
        return removeNote;
    }

    public void setHeader(String header) {
        this.header.setText(header);
    }

}

