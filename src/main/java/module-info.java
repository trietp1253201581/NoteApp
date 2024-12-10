module com.noteapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires com.mailjet.api;
    requires org.json;
    requires okhttp3;
    
    opens com.noteapp to javafx.fxml;
    exports com.noteapp;
    
    opens com.noteapp.controller to javafx.fxml;
    exports com.noteapp.controller;
    
    exports com.noteapp.common.dao;
    exports com.noteapp.common.dbconnection;
    exports com.noteapp.user.model;
    exports com.noteapp.user.dao;
    exports com.noteapp.user.service;
    exports com.noteapp.user.service.security;
    exports com.noteapp.note.model;
    exports com.noteapp.note.dao;
    exports com.noteapp.note.service;
}
