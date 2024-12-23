package com.noteapp.controller;

import com.noteapp.user.dao.UserDAO;
import com.noteapp.user.model.Email;
import com.noteapp.user.model.User;
import com.noteapp.user.service.IUserService;
import com.noteapp.user.service.UserService;
import com.noteapp.user.service.security.MailjetSevice;
import com.noteapp.user.service.security.SixNumCodeGenerator;
import com.noteapp.user.service.security.VerificationMailService;
import com.noteapp.user.service.UserServiceException;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class cho trang Register
 * 
 * @author Nhóm 17
 */
public class RegisterController extends InitableController {
    //Các thuộc tính FXML    
    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button registerButton;
    @FXML
    private TextField schoolField;
    @FXML
    private TextField usernameField;  
    @FXML
    private TextField dayOfBirthField;
    @FXML
    private TextField monthOfBirthField;
    @FXML
    private TextField yearOfBirthField;
    @FXML
    private RadioButton genderMale;
    @FXML
    private RadioButton genderFemale;
    @FXML
    private RadioButton genderOther;
    @FXML
    private TextField emailAddressField;
    @FXML
    private TextField emailNameField;
    @FXML
    private Label errorNameFieldLabel;
    @FXML
    private Label errorUsernameFieldLabel;
    @FXML
    private Label errorPasswordFieldLabel;
    @FXML
    private Label errorBirthdayFieldLabel;
    @FXML
    private Label errorEmailFieldLabel;
    @FXML
    private Label backLoginLabel;
    @FXML
    private Button closeButton;
    
    private IUserService userService;
    private VerificationMailService verificationMailService;
    private User newUser;
    
    @Override
    public void init() {
        userService = new UserService(UserDAO.getInstance());   
        verificationMailService = new VerificationMailService(new MailjetSevice(), 
                    new SixNumCodeGenerator());
        initView();
        registerButton.setOnAction((ActionEvent event) -> {
            register();
        }); 
        closeButton.setOnAction((ActionEvent event) -> {
            close();
        }); 
        backLoginLabel.setOnMouseClicked((MouseEvent event) -> {
            LoginController.open(stage);
        });
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    public void setVerificationMailService(VerificationMailService verificationMailService) {
        this.verificationMailService = verificationMailService;
    }
    
    protected void initView() {
        //Ẩn các error label
        errorNameFieldLabel.setVisible(false);
        errorUsernameFieldLabel.setVisible(false);
        errorPasswordFieldLabel.setVisible(false);
        errorEmailFieldLabel.setVisible(false);
        errorBirthdayFieldLabel.setVisible(false);
        
        //Thiết lập lựa chọn mặc định cho gender
        genderOther.setSelected(true);
        
    }
    
    private void getPasswordFromField() {
        if("".equals(passwordField.getText())) {
            errorPasswordFieldLabel.setVisible(true);
        }
        newUser.setPassword(passwordField.getText());
    }
    
    private void getEmailFromField() {
        Email email = new Email();
        email.setAddress(emailAddressField.getText());
        if(!email.checkAddress()) {
            errorEmailFieldLabel.setVisible(true);
        }
        newUser.setEmail(email);
    }
    
    private void getBirthdayFromField() {
        int dayOfBirth = -1;
        int monthOfBirth = -1;
        int yearOfBirth = -1;
        if(dayOfBirthField.getText().matches("^[0-9]{1,2}$")) {
            dayOfBirth = Integer.parseInt(dayOfBirthField.getText());
        } else if("".equals(dayOfBirthField.getText())) {
            dayOfBirth = LocalDate.now().getDayOfMonth();
        } else {
            errorBirthdayFieldLabel.setVisible(true);
        }
        if(monthOfBirthField.getText().matches("^[0-9]{1,2}$")) {
            monthOfBirth = Integer.parseInt(monthOfBirthField.getText());
        } else if("".equals(monthOfBirthField.getText())) {
            monthOfBirth = LocalDate.now().getMonthValue();
        } else {
            errorBirthdayFieldLabel.setVisible(true);
        }
        if(yearOfBirthField.getText().matches("^[0-9]{4}$")) {
            yearOfBirth = Integer.parseInt(yearOfBirthField.getText());
        } else if("".equals(yearOfBirthField.getText())) {
            yearOfBirth = LocalDate.now().getYear();
        } else {
            errorBirthdayFieldLabel.setVisible(true);
        } 
        if(!errorBirthdayFieldLabel.isVisible()) {
            newUser.setBirthday(Date.valueOf(LocalDate.of(yearOfBirth, monthOfBirth, dayOfBirth)));
        }
    }
    private void getNameFromField() {
        //Láy thông tin name
        if("".equals(nameField.getText())) {
            errorNameFieldLabel.setVisible(true);
        }
        newUser.setName(nameField.getText());
    }
    
    private void getUsernameFromField() {
        if("".equals(usernameField.getText())) {
            errorUsernameFieldLabel.setVisible(true);
        }
        newUser.setUsername(usernameField.getText());
    }
    
    private void getGenderFromBox() {
        if(genderMale.isSelected()) {
            newUser.setGender(User.Gender.MALE);
        } else if (genderFemale.isSelected()) {
            newUser.setGender(User.Gender.FEMALE);
        } else {
            newUser.setGender(User.Gender.OTHER);
        }
    }
    
    private boolean checkErrorAccountInfo() {
        return errorNameFieldLabel.isVisible() || errorPasswordFieldLabel.isVisible() 
                || errorBirthdayFieldLabel.isVisible() || errorEmailFieldLabel.isVisible()
                || errorUsernameFieldLabel.isVisible();
    }
    /**
     * Tiến hành lấy dữ liệu từ các field tương ứng để set dữ liệu cho {@link User},
     * thông qua đó thêm User này vào CSDL
     */
    protected void register() {
        initView();
        //Thiết lập các thuộc tính cho new user
        newUser = new User();
        //Láy thông tin name
        getNameFromField();
        //Lấy username
        getUsernameFromField();
        //Lấy password
        getPasswordFromField();
        //Lấy school
        newUser.setSchool(schoolField.getText());
        //Lấy thông tin về birth
        getBirthdayFromField();
        //Lấy gender
        getGenderFromBox();
        //Lấy email
        getEmailFromField();
        //Kiểm tra xem có lỗi nào không
        if(checkErrorAccountInfo()) {
            return;
        }
        if (!checkVerifyEmail()) {
            return;
        }
        createUser(newUser);
    }
    
    private boolean checkVerifyEmail() {
        VerificationMailService.CodeStatus codeStatus = verifyEmail();
        switch (codeStatus) {
            case EXPIRED -> {
                showAlert(Alert.AlertType.ERROR, "This code is expired!");
            }
            case FALSE -> {
                showAlert(Alert.AlertType.ERROR, "This code is false!");
            }
            case TRUE -> {
                return true;
            }
        }
        return true;
    }

    private void createUser(User newUser) {
        try { 
            //Tạo User mới thành công
            userService.create(newUser);
            showAlert(Alert.AlertType.INFORMATION, "Successfully create");
            Optional<ButtonType> optional = showAlert(Alert.AlertType.CONFIRMATION, "Back to Login");
            if(optional.get() == ButtonType.OK) {
                //Quay về trang đăng nhập
                LoginController.open(stage);
            }          
        } catch (UserServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }
    
    /**
     * Xác thực một Email bằng cách gửi code về địa chỉ được nhập trong field
     * @return Một {@link VerificationMailService.CodeStatus} miêu tả trạng thái
     * của Code được nhập vào dialog
     * @see emailAddressField
     */
    private VerificationMailService.CodeStatus verifyEmail() {
        Email toEmail = new Email();
        toEmail.setAddress(emailAddressField.getText());
        toEmail.setName(emailNameField.getText());
        verificationMailService.sendCode(toEmail);
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("VERIFY CODE HAS SENT TO YOUR EMAIL");
        dialog.setHeaderText("Enter your verification code");
        //Lấy kết quả
        Optional<String> confirm = dialog.showAndWait();
        confirm.ifPresent(inputCode -> {
            verificationMailService.checkCode(inputCode);
        });
        return verificationMailService.getCodeStatus();
    }
    
    /**
     * Mở trang register
     * @param stage Stage chứa trang register này
     */
    public static void open(Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "RegisterView.fxml";
            
            RegisterController controller = new RegisterController();

            controller.setStage(stage);
            controller.loadFXMLAndSetScene(filePath);
            controller.init();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open register");
        }
    }
}