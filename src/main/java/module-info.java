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
}
