package com.noteapp;

import com.noteapp.controller.LoginController;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Class chính của ứng dụng, tạo ứng dụng chạy cho user
 * @author Nhóm 17
 */
public class NoteApp extends Application {
    
    /**
     * Khởi tạo một Stage, bắt đầu chạy ứng dụng và chuyển tới trang Login
     * @param primaryStage Stage khởi tạo
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);
        LoginController.open(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}