package com.noteapp.controller;

import com.noteapp.user.service.security.VerificationMailService;
import com.noteapp.note.service.NoteService;
import com.noteapp.note.service.ShareNoteService;
import com.noteapp.user.service.UserService;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 *
 * @author admin
 */
public class Controller {
    protected double posX, posY;
    protected UserService userService;
    protected NoteService noteService;
    protected ShareNoteService shareNoteService;
    protected VerificationMailService verificationMailService;
    protected Stage stage;
    protected Scene scene;
    
    public static final String DEFAULT_FXML_RESOURCE = "/com/noteapp/view/";
    
    public void init() {
        initServerService();
    }
    
    public void initServerService() {
        userService = new UserService();
        noteService = new NoteService();
        shareNoteService = new ShareNoteService();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public static Optional<ButtonType> showAlert(Alert.AlertType alertType, String text) {
        Alert alert = new Alert(alertType);
        alert.setTitle(String.valueOf(alertType));
        alert.setHeaderText(text);
        return alert.showAndWait();
    }
    
    public void setSceneMoveable() {
        posX = 0;
        posY = 0;
        scene.setOnMousePressed((MouseEvent mouseEvent) -> {
            posX = mouseEvent.getSceneX();
            posY = mouseEvent.getSceneY();
        });       
        scene.setOnMouseDragged((MouseEvent mouseEvent) -> {
            stage.setX(mouseEvent.getScreenX() - posX);
            stage.setY(mouseEvent.getScreenY() - posY);
        });
    }
    
    public void setResource(URL fXMLLocation) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(fXMLLocation);
        scene = new Scene(loader.load());
    }
           
    protected void close() {
        Optional<ButtonType> optional = showAlert(Alert.AlertType.CONFIRMATION,
                "Close NoteLite?");
        if(optional.get() == ButtonType.OK) {
            System.exit(0);
        }
    }
    
    public void loadFXMLAndSetScene(String filePath, Object controller) throws IOException {
        Parent root = this.loadFXML(filePath, controller);
        scene = new Scene(root);
        setSceneMoveable();
        stage.setScene(scene);
    }
    
    public <T extends Parent> T loadFXML(String filePath, Object controller) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(filePath));
        loader.setController(controller);
        T root = loader.load();
        return root;
    }
    
    public void showFXML() {
        stage.show();
    }
}
