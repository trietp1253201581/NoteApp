package com.noteapp.controller;

import com.noteapp.common.service.NoteAppService;
import com.noteapp.common.service.NoteAppServiceException;
import com.noteapp.user.dao.UserDAO;
import com.noteapp.user.model.Email;
import com.noteapp.user.service.UserService;
import com.noteapp.user.service.security.MailjetSevice;
import com.noteapp.user.service.security.SixNumCodeGenerator;
import com.noteapp.user.service.security.VerificationMailService;
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
public class ResetPasswordController extends RequestServiceController implements Initable {
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
    
    @Override
    public void init() {
        noteAppService = new NoteAppService();
        noteAppService.setUserService(new UserService(UserDAO.getInstance()));
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
            Email vefiryEmail = noteAppService.getUserService().getVerificationEmail(username);
            noteAppService.setVerificationMailService(new VerificationMailService(new MailjetSevice(), 
                    new SixNumCodeGenerator()));
            noteAppService.getVerificationMailService().sendCode(vefiryEmail);
            verificationCodeField.setEditable(true);
        } catch (NoteAppServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }
    
    /**
     * Kiểm tra code xác thực được nhập vào từ người dùng.
     * Nếu code đúng thì mở khóa field để nhập password mới
     */
    protected void checkVerifyCode() {
        if(!verificationCodeField.isEditable()) {
            return;
        }
        try {
            String inputCode = verificationCodeField.getText();
            noteAppService.getVerificationMailService().checkCode(inputCode);
            VerificationMailService.CodeStatus codeStatus = noteAppService.getVerificationMailService().getCodeStatus();
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
        } catch (NoteAppServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }
    
    protected void resetPassword() {
        if(!passwordField.isEditable()) {
            return;
        }
        String username = usernameField.getText();
        try {
            noteAppService.getUserService().updatePassword(username, passwordField.getText());
            initScene();
            showAlert(Alert.AlertType.INFORMATION, "Successfully reset.");
            LoginController.open(stage);
        } catch (NoteAppServiceException ex) {
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
