/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.noteapp.controller;

import com.noteapp.note.model.Note;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 *
 * @author admin
 */
public class RecentlyNoteCardController extends Controller {
    @FXML
    private Text header;
    @FXML
    private Text lastModifiedDate;
    @FXML
    private Text editor;
    
    private Note note;
    
    @Override
    public void init() {
        
    }
    
    public void setData(Note note) {
        this.note = note;
        header.setText(note.getHeader());
        lastModifiedDate.setText(note.getLastModifiedDate().toString());
        editor.setText(note.getAuthor());
    }
    
    public Note getNote() {
        return note;
    }
}