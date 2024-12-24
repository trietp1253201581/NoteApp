package com.noteapp.controller;

import com.noteapp.service.NoteAppService;
import com.noteapp.service.NoteAppServiceException;
import com.noteapp.user.dao.AdminDAO;
import com.noteapp.user.dao.UserDAO;
import com.noteapp.user.model.User;
import com.noteapp.user.service.AdminService;
import com.noteapp.user.service.UserService;
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
 * FXML Controller class cho trang Login
 * @author Nhóm 17
 */
public class LoginController extends RequestServiceController implements Initable {   
    //Các thuộc tính FXML
    @FXML
    private Button loginButton;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;    
    @FXML 
    private Label registerLabel;
    @FXML
    private Button closeButton;
    @FXML
    private Label forgotPasswordLabel;
        
    @Override
    public void init() {
        noteAppService = new NoteAppService();
        noteAppService.setUserService(new UserService(UserDAO.getInstance()));
        noteAppService.setAdminService(new AdminService(UserDAO.getInstance(), AdminDAO.getInstance()));
        loginButton.setOnAction((ActionEvent event) -> {
            login();
        });
        closeButton.setOnAction((ActionEvent event) -> {
            close();
        });
        registerLabel.setOnMouseClicked((MouseEvent event) -> {
            RegisterController.open(stage);
        });
        forgotPasswordLabel.setOnMouseClicked((MouseEvent event) -> {
            ResetPasswordController.open(stage);
        });
    }

    /**
     * Kiểm tra username và password của User thông qua các field
     * và tiến hành chuyển vào trang dashboard tương ứng nếu đăng nhập thành công
     * @see usernameField
     * @see passwordField
     */
    protected void login() {  
        //Lấy username và password
        String username = usernameField.getText();
        String password = passwordField.getText();
      
        //Kiểm tra thông tin đăng nhập
        try { 
            boolean isAdmin = noteAppService.getAdminService().isAdmin(username);
            if (!isAdmin) {
                User user = noteAppService.getUserService().checkUser(username, password);
                showAlert(Alert.AlertType.INFORMATION, "Successfully Login");
                //Mở Dashboard của user này
                DashboardController.open(user, stage);
            } else {
                noteAppService.getAdminService().checkAdmin(username, password);
                showAlert(Alert.AlertType.INFORMATION, "Successfully Login");
                //Mở Dashboard của user này
                AdminDashboardController.open(stage);
            }
            
        } catch (NoteAppServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }
    
    /**
     * Mở một trang đăng nhập
     * @param stage Stage chứa trang đăng nhập này
     */
    public static void open(Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "LoginView.fxml";
            
            LoginController controller = new LoginController();

            controller.setStage(stage);
            controller.loadFXMLAndSetScene(filePath);
            controller.init();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open login");
        }
    }
}