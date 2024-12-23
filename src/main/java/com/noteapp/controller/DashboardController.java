package com.noteapp.controller;

import com.noteapp.note.dao.NoteBlockDAO;
import com.noteapp.note.dao.NoteDAO;
import com.noteapp.note.dao.NoteFilterDAO;
import com.noteapp.note.dao.ShareNoteDAO;
import com.noteapp.note.dao.SurveyBlockDAO;
import com.noteapp.note.dao.TextBlockDAO;
import com.noteapp.user.model.Email;
import com.noteapp.note.model.Note;
import com.noteapp.note.model.NoteBlock;
import com.noteapp.note.model.NoteFilter;
import com.noteapp.note.model.ShareNote;
import com.noteapp.note.model.TextBlock;
import com.noteapp.note.service.INoteService;
import com.noteapp.note.service.IShareNoteService;
import com.noteapp.note.service.NoteService;
import com.noteapp.user.model.User;
import com.noteapp.note.service.NoteServiceException;
import com.noteapp.note.service.ShareNoteService;
import com.noteapp.user.dao.UserDAO;
import com.noteapp.user.service.IUserService;
import com.noteapp.user.service.UserService;
import com.noteapp.user.service.UserServiceException;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class cho Dashboard của User
 * @author Nhóm 17
 */
public class DashboardController extends InitableController {
    //Các thuộc tính FXML của form dashboard chung
    @FXML 
    private BorderPane extraServiceScene;
    @FXML
    private VBox mainScene;
    //Các thuộc tính chung
    @FXML
    private Label userLabel;   
    @FXML
    private Button closeButton;
    @FXML
    private Button logoutButton;
    @FXML
    private Button backMainSceneButton;
    @FXML
    private Button myNotesButton;
    @FXML
    private Button myAccountButton;
    @FXML
    private Button homeButton;
    //Các thuộc tính của myNotesScene
    @FXML
    private AnchorPane myNotesScene;
    @FXML
    private TextField searchNoteField;  
    @FXML
    private VBox noteCardLayout;   
    @FXML
    private Button createNoteButton;   
    @FXML
    private Button deleteNoteButton; 
    //Các thuộc tính của myAccountScene
    @FXML
    private AnchorPane myAccountScene;    
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailAddressField;
    @FXML
    private TextField nameField;
    @FXML 
    private TextField dayOfBirthField;
    @FXML
    private TextField monthOfBirthField;
    @FXML
    private TextField yearOfBirthField;
    @FXML
    private TextField schoolField;
    @FXML
    private RadioButton genderMale;
    @FXML
    private RadioButton genderFemale;
    @FXML
    private RadioButton genderOther;
    @FXML
    private Label errorEmailFieldLabel;
    @FXML
    private Label errorNameFieldLabel;
    @FXML
    private Label errorPasswordFieldLabel;
    @FXML
    private Label errorBirthdayFieldLabel;
    @FXML
    private Button changePasswordButton;
    @FXML
    private Button saveAccountButton;  
    
    @FXML
    private FlowPane recentlyVisitedLayout;
    @FXML
    private AnchorPane homeScene;
    @FXML
    private StackPane stackLayout;
    
    private User myUser;   
    private Note currentNote;
    private List<Note> myNotes;   
    private List<ShareNote> myReceivedNotes;
    private List<Note> openedNotes;
    
    private IUserService userService;
    private INoteService noteService;
    private IShareNoteService shareNoteService;
    
    @Override
    public void init() {
        userService = new UserService(UserDAO.getInstance());
        noteService = new NoteService(NoteDAO.getInstance(), NoteFilterDAO.getInstance(), 
                NoteBlockDAO.getInstance(), TextBlockDAO.getInstance(), SurveyBlockDAO.getInstance());
        shareNoteService = new ShareNoteService(ShareNoteDAO.getInstance(),  
                NoteDAO.getInstance(), NoteFilterDAO.getInstance(), 
                NoteBlockDAO.getInstance(), TextBlockDAO.getInstance(), SurveyBlockDAO.getInstance());
        initView();
        closeButton.setOnAction((ActionEvent event) -> {
            super.close();
        });
        //Switch
        myNotesButton.setOnAction((ActionEvent event) -> {
            changeSceneInExtraScene(myNotesButton);
            try {
                myNotes = noteService.getAll(myUser.getUsername());
            } catch (NoteServiceException ex) {
                myNotes = new ArrayList<>();
            }
            //Lấy tất cả các note được share tới myUser này
            try { 
                myReceivedNotes = shareNoteService.getAllReceived(myUser.getUsername());
            } catch (NoteServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
            initMyNotesScene(myNotes, myReceivedNotes);
        });
        myAccountButton.setOnAction((ActionEvent event) -> {
            changeSceneInExtraScene(myAccountButton);
            initMyAccountScene(myUser);
        });
        homeButton.setOnAction((ActionEvent event) -> {
            changeSceneInExtraScene(homeButton);
            try {
                myNotes = noteService.getAll(myUser.getUsername());
            } catch (NoteServiceException ex) {
                myNotes = new ArrayList<>();
            }
            initHomeScene(myNotes);
        });
        //My Note Scene
        searchNoteField.setOnAction((ActionEvent event) -> {
            searchNote();
        });
        createNoteButton.setOnAction((ActionEvent event) -> {
            createNote();
        });
        deleteNoteButton.setOnAction((ActionEvent event) -> {
            deleteNote();
        });
        //My Account Scene
        changePasswordButton.setOnAction((ActionEvent event) -> {
            changePassword();
        });
        saveAccountButton.setOnAction((ActionEvent event) -> {
            saveAccount();
        });
       
        //Other
        backMainSceneButton.setOnAction((ActionEvent event) -> {
            if (currentNote.isDefaultValue()) {
                showAlert(Alert.AlertType.ERROR, "No note has chosen!");
                return;
            }
            if (!currentNote.isPubliced()) {
                EditNoteController.open(myUser, currentNote, openedNotes, stage);
            } else {
                EditShareNoteController.open(myUser, (ShareNote) currentNote, openedNotes, stage);
            }
        });
        logoutButton.setOnAction((ActionEvent event) -> {
            LoginController.open(stage);
        });
    }
    
    protected void initView() {
        userLabel.setText(myUser.getName());
        try {           
            myNotes = noteService.getAll(myUser.getUsername());
        } catch (NoteServiceException ex) {
            myNotes = new ArrayList<>();
        }
        initHomeScene(myNotes);
        changeSceneInExtraScene(homeButton);
    }
    
    private void openNote(int noteId, Optional<ButtonType> optional) {
        if(optional.get() == ButtonType.OK) {                        
            try {
                currentNote = noteService.open(noteId);
                if (!openedNotes.contains(currentNote)) {
                    openedNotes.add(currentNote);
                }
                if (currentNote.isPubliced()) {
                    currentNote = shareNoteService.open(noteId, myUser.getUsername());
                    EditShareNoteController.open(myUser, (ShareNote) currentNote, openedNotes, stage);
                } else {                                
                    EditNoteController.open(myUser, currentNote, openedNotes, stage);
                }
            } catch (NoteServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        }       
    }
    
    /**
     * Khởi tạo Scene My Notes, chứa danh sách các Note mà User sở hữu hoặc các
     * Note được chia sẻ tới User này
     * @param notes Danh sách các Note mà User sở hữu
     * @param shareNotes Danh sách các Note được chia sẻ tới User này
     */
    protected void initMyNotesScene(List<Note> notes, List<ShareNote> shareNotes) {        
        //Làm sạch layout
        noteCardLayout.getChildren().clear();
        if(notes.isEmpty() || shareNotes.isEmpty()) {
            return;
        }
        //Load các Note Card
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "NoteCardView.fxml";
        for(int i=0; i < notes.size(); i++) {
            //Load Note Card Layout
            try {
                //Thiết lập dữ liệu cho Note Card
                NoteCardController controller = new NoteCardController();
                
                HBox box = controller.loadFXML(filePath);
                Note note = notes.get(i);
                controller.setData(note);
                //Xử lý khi nhấn vào note card
                box.setOnMouseClicked((MouseEvent event) -> {
                    //Tạo thông báo và mở note nếu chọn OK
                    Optional<ButtonType> optional = showAlert(Alert.AlertType.CONFIRMATION, 
                            "Open " + controller.getHeader());
                    openNote(controller.getId(), optional);
                });
                //Thêm Note Card vào layout
                noteCardLayout.getChildren().add(box);
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Can load note!");
            }
        }      
        for(int i=0; i < shareNotes.size(); i++) {
            //Load Note Card Layout
            if (shareNotes.get(i).getAuthor().equals(myUser.getUsername())) {
                continue;
            }
            try {
                //Thiết lập dữ liệu cho Note Card
                NoteCardController controller = new NoteCardController();
                
                HBox box = controller.loadFXML(filePath);
                controller.setData(shareNotes.get(i));
                //Xử lý khi nhấn vào note card
                box.setOnMouseClicked((MouseEvent event) -> {
                    //Tạo thông báo và mở note nếu chọn OK
                    Optional<ButtonType> optional = showAlert(Alert.AlertType.CONFIRMATION, 
                            "Open " + controller.getHeader());
                    openNote(controller.getId(), optional);
                });
                //Thêm Note Card vào layout
                noteCardLayout.getChildren().add(box);
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Can load note!");
            }
        }        
    }
    
    /**
     * Khởi tạo Scene Home 
     * @param recentlyNotes Các Note được truy cập gần nhất
     */
    protected void initHomeScene(List<Note> recentlyNotes) {
        recentlyVisitedLayout.getChildren().clear();
        if (recentlyNotes.isEmpty()) {
            return;
        }
        if (recentlyNotes.size() > 3) {
            recentlyNotes = recentlyNotes.subList(0, 3);
        }
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "RecentlyNoteCardView.fxml";
        for (Note note: recentlyNotes) {
            try {
                RecentlyNoteCardController controller = new RecentlyNoteCardController();
                VBox box = controller.loadFXML(filePath);
                controller.setNote(note);
                box.setOnMouseClicked((event) -> {
                    Optional<ButtonType> optional = showAlert(Alert.AlertType.CONFIRMATION, 
                            "Open " + controller.getNote().getHeader());
                    openNote(controller.getNote().getId(), optional);
                });
                recentlyVisitedLayout.getChildren().add(box);
            } catch (IOException ex) {
            }
        }
    }
    
    /**
     * Khởi tạo scene hiển thị các thông tin của User
     * @param user User đang đăng nhập
     */
    protected void initMyAccountScene(User user) {
        //Thiết lập các thuộc tính
        usernameField.setText(user.getUsername());
        usernameField.setEditable(false);
        passwordField.setText(user.getPassword());
        emailAddressField.setText(user.getEmail().getAddress());
        nameField.setText(user.getName());
        schoolField.setText(user.getSchool());
        dayOfBirthField.setText(String.valueOf(user.getBirthday().toLocalDate().getDayOfMonth()));
        monthOfBirthField.setText(String.valueOf(user.getBirthday().toLocalDate().getMonthValue()));
        yearOfBirthField.setText(String.valueOf(user.getBirthday().toLocalDate().getYear()));
        switch(user.getGender()) {
            case User.Gender.MALE -> {
                genderMale.setSelected(true);
            }
            case User.Gender.FEMALE -> {
                genderFemale.setSelected(true);
            }
            case User.Gender.OTHER -> {
                genderOther.setSelected(true);
            }
        }
        //Ẩn các error
        errorEmailFieldLabel.setVisible(false);
        errorBirthdayFieldLabel.setVisible(false);
        errorNameFieldLabel.setVisible(false);
        errorPasswordFieldLabel.setVisible(false);
    }
    
    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }
    
    public void setCurrentNote(Note currentNote) {
        this.currentNote = currentNote;
    }
    
    public void setOpenedNotes(List<Note> openedNotes) {
        this.openedNotes = openedNotes;
    }
    
    private boolean checkMatchSearchText(Note note, String searchText) {
        if(note.getHeader().contains(searchText)) { 
            return true;
        } else {
            for(NoteFilter noteFilter: note.getFilters()) {
                if(noteFilter.getFilter().contains(searchText)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Tìm kiếm một note có header hoặc filter chứa văn bản
     * đang được nhập vào trong field
     * @see searchNoteField
     */
    protected void searchNote() {
        //Lấy thông tin cần search
        String searchText = searchNoteField.getText();
        //Tạo list mới để chứa các note hợp lệ
        List<Note> notes = new ArrayList<>();
        List<ShareNote> shareNotes = new ArrayList<>();
        //Thêm các note hợp lệ vào list
        for(Note newNote: myNotes) {
            if (checkMatchSearchText(newNote, searchText)) {
                notes.add(newNote);
            }
        }
        for (ShareNote newShareNote: myReceivedNotes) {
            if (checkMatchSearchText(newShareNote, searchText)) {
                shareNotes.add(newShareNote);
            }
        }
        //Load lại My Notes Scene
        initMyNotesScene(notes, shareNotes);
    }
    
    protected void createNote() {
        //Hiện dialog để nhập header cho Note mới
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create new note");
        dialog.setHeaderText("Enter header for your new note");
        //Lấy kết quả
        Optional<String> confirm = dialog.showAndWait();
        //Xử lý kết quả khi nhấn OK
        confirm.ifPresent(selectedHeader -> {
            //Set dữ liệu cho note mới
            Note newNote = new Note();
            newNote.setAuthor(myUser.getUsername());
            newNote.setHeader(selectedHeader);
            newNote.getBlocks().add(
                    new TextBlock("EditHere", -1, 
                            "B1", myUser.getUsername(), NoteBlock.BlockType.TEXT, 1));
            newNote.setLastModifiedDate(Date.valueOf(LocalDate.now()));
            //Tạo Note mới
            try { 
                //Tạo thành công
                newNote = noteService.create(newNote);
                showAlert(Alert.AlertType.INFORMATION, "Successfully create " + newNote.getHeader());
                //Thêm vào list và load lại
                myNotes.add(newNote);
                initMyNotesScene(myNotes, myReceivedNotes);
            } catch (NoteServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });
    }

    protected void deleteNote() {
        //Lấy list các header note
        List<String> myNotesHeader = new ArrayList<>();
        for(Note note: myNotes){
            myNotesHeader.add(note.getHeader());
        }
        //Hiện dialog để chọn note cần xóa
        ChoiceDialog<String> dialog = new ChoiceDialog<>(myNotesHeader.get(0), myNotesHeader);
        dialog.setTitle("Delete Note");
        dialog.setHeaderText("Choose note to delete");
        //Lấy kết quả
        Optional<String> confirm = dialog.showAndWait();
        //Xử lý kết quả khi nhấn OK
        confirm.ifPresent(selectedHeader -> {
            //Xóa Note được chọn
            try { 
                //Xóa thành công
                Note deletedNote = new Note();
                for(Note note: myNotes) {
                    if(note.getHeader().equals(selectedHeader)) {
                        deletedNote = note;
                    }
                }
                noteService.delete(deletedNote.getId());
                showAlert(Alert.AlertType.INFORMATION, "Successfully delete " + deletedNote.getHeader());
                //Xóa khỏi list và load lại
                myNotes.remove(deletedNote);
                //Load lại My Notes Scene
                initMyNotesScene(myNotes, myReceivedNotes);
            } catch (NoteServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });
    }
    
    private void getPasswordFromField() {
        if("".equals(passwordField.getText())) {
            errorPasswordFieldLabel.setVisible(true);
        }
        myUser.setPassword(passwordField.getText());
    }
    
    private void getEmailFromField() {
        Email email = new Email();
        email.setAddress(emailAddressField.getText());
        if(!email.checkAddress()) {
            errorEmailFieldLabel.setVisible(true);
        }
        myUser.setEmail(email);
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
            myUser.setBirthday(Date.valueOf(LocalDate.of(yearOfBirth, monthOfBirth, dayOfBirth)));
        }
    }
    
    private void getNameFromField() {
        //Láy thông tin name
        if("".equals(nameField.getText())) {
            errorNameFieldLabel.setVisible(true);
        }
        myUser.setName(nameField.getText());
    }
    
    private void getGenderFromBox() {
        if(genderMale.isSelected()) {
            myUser.setGender(User.Gender.MALE);
        } else if (genderFemale.isSelected()) {
            myUser.setGender(User.Gender.FEMALE);
        } else {
            myUser.setGender(User.Gender.OTHER);
        }
    }
    
    private boolean checkErrorAccountInfo() {
        return errorNameFieldLabel.isVisible() || errorPasswordFieldLabel.isVisible() 
                || errorBirthdayFieldLabel.isVisible() || errorEmailFieldLabel.isVisible();
    }

    protected void saveAccount() {
        errorEmailFieldLabel.setVisible(false);
        errorBirthdayFieldLabel.setVisible(false);
        errorNameFieldLabel.setVisible(false);
        errorPasswordFieldLabel.setVisible(false);
        //Lấy password
        getPasswordFromField();
        //Lấy Email
        getEmailFromField();
        //Lấy name
        getNameFromField();
        //Lấy school
        myUser.setSchool(schoolField.getText());
        //Lấy thông tin về birth
        getBirthdayFromField();
        //Lấy gender
        getGenderFromBox();
        //Kiểm tra xem có lỗi nào không
        if(checkErrorAccountInfo()) {
            return;
        }
        //Cập nhật User
        try { 
            //Cập nhật thành công
            userService.update(myUser);
            showAlert(Alert.AlertType.INFORMATION, "Successfully update for " + myUser.getUsername());
        } catch (UserServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }
    
    protected void changePassword() {
        //Hiện dialog để nhập mật khẩu cũ
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your present password");
        //Lấy kết quả
        Optional<String> confirm = dialog.showAndWait();
        //Xử lý kết quả
        confirm.ifPresent(password -> {
            //Nếu nhập đúng thì cho phép nhập mật khẩu mới
            if(password.equals(myUser.getPassword())) {
                passwordField.setEditable(true);
            }
        });      
    }
 
    /**
     * Thay đổi Scene trên dashboard dựa vào việc nút nào đang được bấm
     * @param button Nút được bấm
     */
    private void changeSceneInExtraScene(Button button) {
        String pressedStyle = "-fx-background-color: #1a91b8";
        String unPressedStyle = "-fx-background-color: transparent";
        //init lại
        myNotesButton.setStyle(unPressedStyle);
        myNotesScene.setVisible(false);
        myAccountButton.setStyle(unPressedStyle);
        myAccountScene.setVisible(false);
        homeButton.setStyle(unPressedStyle);
        homeScene.setVisible(false);
        //Press button được chọn và chuyển scene tương ứng
        if (button == myNotesButton) {
            myNotesButton.setStyle(pressedStyle);
            myNotesScene.setVisible(true);
        } else if (button == myAccountButton) {          
            myAccountButton.setStyle(pressedStyle);
            myAccountScene.setVisible(true);
        } else if (button == homeButton) {
            homeButton.setStyle(pressedStyle);
            homeScene.setVisible(true);
        } 
    }
    
    /**
     * Mở một Dashboard cho User
     * @param myUser User đã đăng nhập thành công
     * @param stage Stage chứa Dashboard này
     */
    public static void open(User myUser, Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "DashboardView.fxml";
            
            DashboardController controller = new DashboardController();

            controller.setStage(stage);
            controller.setMyUser(myUser);
            controller.setCurrentNote(new Note());
            controller.setOpenedNotes(new ArrayList<>());
            controller.loadFXMLAndSetScene(filePath);
            controller.init();
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open dashboard");
        }
    }
    
    /**
     * Mở một Dashboard cho User khi đang Edit
     * @param myUser User đang trong phiên đăng nhập
     * @param currentNote Note được edit hiện tại
     * @param openedNotes Các Note đang được mở
     * @param stage Stage chứa Dashboard này
     */
    public static void open(User myUser, Note currentNote, List<Note> openedNotes, Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "DashboardView.fxml";
            
            DashboardController controller = new DashboardController();

            controller.setStage(stage);
            controller.setMyUser(myUser);
            controller.setOpenedNotes(openedNotes);
            controller.setCurrentNote(currentNote);
            controller.loadFXMLAndSetScene(filePath);
            
            controller.init();         
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open dashboard");
        }
    }
}