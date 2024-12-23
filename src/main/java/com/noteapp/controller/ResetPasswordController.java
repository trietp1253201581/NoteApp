package com.noteapp.controller;

import com.noteapp.user.dao.UserDAO;
import com.noteapp.user.model.Email;
import com.noteapp.user.service.IUserService;
import com.noteapp.user.service.UserService;
import com.noteapp.user.service.security.MailjetSevice;
import com.noteapp.user.service.security.SixNumCodeGenerator;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Một Controller cho trang cấp lại mật khẩu mới
 * @author Nhóm 17
 */
public class ResetPasswordController extends InitableController {
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
    @FXML
    private Label backLoginLabel;

    protected IUserService userService;
    protected VerificationMailService verificationMailService;
    
    @Override
    public void init() {
        userService = new UserService(UserDAO.getInstance());
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
        backLoginLabel.setOnMouseClicked((MouseEvent event) -> {
            LoginController.open(stage);
        });
    }
    
    protected void initScene() {
        errorUsernameFieldLabel.setVisible(false);
        verificationCodeField.setEditable(false);
        passwordField.setEditable(false);
    }
    
    /**
     * Gửi một verify code tới email của User có username được nhập vào field,
     * nếu user tồn tại và đã gửi email thì mở khóa field để nhập code
     */
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
                    new SixNumCodeGenerator()
            );
            verificationMailService.sendCode(vefiryEmail);
            verificationCodeField.setEditable(true);
        } catch (UserServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }
    
    /**
     * Kiểm tra code xác thực được nhập vào từ người dùng.
     * Nếu code đúng thì mở khóa field để nhập password mới
     */
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
            controller.loadFXMLAndSetScene(filePath);
            controller.init();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open reset Password");
        }
    }
}
