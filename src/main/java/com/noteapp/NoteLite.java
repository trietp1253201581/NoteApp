package com.noteapp;

import com.noteapp.controller.Controller;
import com.noteapp.controller.LoginController;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Class chính của project, tạo ứng dụng chạy cho user
 * @author Nhóm 17
 */
public class NoteLite extends Application {
    /**
     * @param primaryStage Stage khởi tạo
     */
    @Override
    public void start(Stage primaryStage) {
        try {          
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "LoginView.fxml";
            
            LoginController controller = new LoginController();
            primaryStage.initStyle(StageStyle.UNDECORATED);
            controller.setStage(primaryStage);
            controller.loadFXMLAndSetScene(filePath, controller);
            controller.init();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            ex.printStackTrace();
            Controller.showAlert(Alert.AlertType.ERROR, "Can't open application");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}