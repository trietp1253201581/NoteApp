package com.noteapp.controller;

import com.noteapp.user.model.Email;
import com.noteapp.user.service.security.MailjetSevice;
import com.noteapp.user.service.security.SixNumVerificationCodeService;
import com.noteapp.user.service.security.VerificationMailService;
import com.noteapp.user.service.UserServiceException;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ResetPasswordController extends Controller {
    @FXML
    private Button closeButton;
    @FXML
    private Button confirmPasswordButton;
    @FXML
    private Label errorUsernameFieldLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button sendCodeButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField verificationCodeField;
    @FXML
    private Button verifyCodeButton;

    @Override
    public void init() {
        initServerService();
        initScene();
        closeButton.setOnAction((ActionEvent event) -> {
            close();
        }); 
        sendCodeButton.setOnAction((ActionEvent event) -> {
            sendCode();
        }); 
        verifyCodeButton.setOnAction((ActionEvent event) -> {
            checkVerifyCode();
        }); 
        confirmPasswordButton.setOnAction((ActionEvent event) -> {
            resetPassword();
        }); 
    }
    
    protected void initScene() {
        errorUsernameFieldLabel.setVisible(false);
        verificationCodeField.setEditable(false);
        passwordField.setEditable(false);
    }
    
    protected void sendCode() {
        String username = usernameField.getText();
        if("".equals(username)) {
            errorUsernameFieldLabel.setVisible(true);
            return;
        } else {
            errorUsernameFieldLabel.setVisible(false);
        }
        try {
            Email vefiryEmail = userService.getVerificationEmail(username);
            verificationMailService = new VerificationMailService(
                    new MailjetSevice(),
                    new SixNumVerificationCodeService()
            );
            verificationMailService.sendCode(vefiryEmail);
            verificationCodeField.setEditable(true);
        } catch (UserServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }
    
    protected void checkVerifyCode() {
        if(verificationMailService == null) {
            return;
        }
        if(!verificationCodeField.isEditable()) {
            return;
        }
        String inputCode = verificationCodeField.getText();
        verificationMailService.checkCode(inputCode);
        VerificationMailService.CodeStatus codeStatus = verificationMailService.getCodeStatus();
        switch (codeStatus) {
                case EXPIRED -> {
                    showAlert(Alert.AlertType.ERROR, "This code is expired!");
                }
                case FALSE -> {
                    showAlert(Alert.AlertType.ERROR, "This code is false!");
                }
                case TRUE -> {
                    showAlert(Alert.AlertType.INFORMATION, "Please input your new password below");
                    passwordField.setEditable(true);
                }
            }
    }
    
    protected void resetPassword() {
        if(!passwordField.isEditable()) {
            return;
        }
        String username = usernameField.getText();
        try {
            userService.updatePassword(username, passwordField.getText());
            initScene();
            showAlert(Alert.AlertType.INFORMATION, "Successfully reset.");
            LoginController.open(stage);
        } catch (UserServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }
    
    public static void open(Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "ResetPasswordView.fxml";
            
            ResetPasswordController controller = new ResetPasswordController();

            controller.setStage(stage);
            controller.loadFXMLAndSetScene(filePath, controller);
            controller.init();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Can't open reset Password");
        }
    }
}
