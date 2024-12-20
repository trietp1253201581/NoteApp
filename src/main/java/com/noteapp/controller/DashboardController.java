package com.noteapp.controller;

import com.noteapp.user.model.Email;
import com.noteapp.note.model.Note;
import com.noteapp.note.model.NoteBlock;
import com.noteapp.note.model.NoteFilter;
import com.noteapp.note.model.ShareNote;
import com.noteapp.note.model.TextBlock;
import com.noteapp.user.model.User;
import com.noteapp.note.service.NoteServiceException;
import com.noteapp.user.service.UserServiceException;
import java.io.File;
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
import javafx.scene.control.ComboBox;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class cho Dashboard GUI
 * 
 * @author Nhóm 23
 * @since 07/04/2024
 * @version 1.0
 */
public class DashboardController extends Controller {
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
    @FXML
    private Button shareNoteButton;
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
    //Các thuộc tính FXML của importExportScene
    @FXML
    private AnchorPane importExportScene;
    @FXML
    private Button exportFileButton;
    @FXML
    private ComboBox<String> exportNoteComboBox;
    @FXML
    private ComboBox<String> exportFormatComboBox;
    @FXML
    private Label importNoteName;
    @FXML
    private Button chooseInputFileButton;
    @FXML
    private Button importFileButton;
    @FXML
    private Label importFileName;
    //Các thuộc tính FXML của shareNoteScene
    @FXML
    private AnchorPane shareNoteScene;
    @FXML
    private ComboBox<String> chooseShareNoteComboBox;
    @FXML
    private TextField chooseUserShareField;
    @FXML
    private RadioButton shareTypeReadOnly;
    @FXML
    private RadioButton shareTypeCanEdit;
    @FXML
    private Button sendNoteButton;
    @FXML
    private VBox shareNoteCardLayout;
    
    @FXML
    private FlowPane recentlyVisitedLayout;
    @FXML
    private AnchorPane homeScene;
    @FXML
    private StackPane stackLayout;
    
    private User myUser;   
    private Note currentNote;
    private List<Note> myNotes;   
    private List<ShareNote> mySharedNotes;
    private List<Note> openedNotes;
    
    @Override
    public void init() {
        initServerService();
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
                mySharedNotes = shareNoteService.getAllReceived(myUser.getUsername());
            } catch (NoteServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
            initMyNotesScene(myNotes, mySharedNotes);
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
        shareNoteButton.setOnAction((ActionEvent event) -> {
            //Chuyển sang Scene ShareNote
            changeSceneInExtraScene(shareNoteButton);
            //Lấy tất cả các note của myUser
            try { 
                //Lấy thành công
                myNotes = noteService.getAll(myUser.getUsername());
            } catch (NoteServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
            //Lấy tất cả các note được share tới myUser này
            try { 
                mySharedNotes = shareNoteService.getAllReceived(myUser.getUsername());
            } catch (NoteServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
            //Init lại Scene
            initShareNoteScene(myNotes, mySharedNotes);
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
        //Import export
        exportFileButton.setOnAction((ActionEvent event) -> {
            exportFile();
        });
        importFileButton.setOnAction((ActionEvent event) -> {
            importFile();
        });
        chooseInputFileButton.setOnAction((ActionEvent event) -> {
            chooseFileToInput();
        });
        //Share Note
        sendNoteButton.setOnAction((ActionEvent event) -> {
            sendNote();
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
    
    public void initView() {
        userLabel.setText(myUser.getName());
        try {           
            myNotes = noteService.getAll(myUser.getUsername());
        } catch (NoteServiceException ex) {
            myNotes = new ArrayList<>();
        }
        initHomeScene(myNotes);
        
        changeSceneInExtraScene(homeButton);
    }
    
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
                
                HBox box = controller.loadFXML(filePath, controller);
                Note note = notes.get(i);
                controller.setData(note);
                //Xử lý khi nhấn vào note card
                box.setOnMouseClicked((MouseEvent event) -> {
                    //Tạo thông báo và mở note nếu chọn OK
                    Optional<ButtonType> optional = showAlert(Alert.AlertType.CONFIRMATION, 
                            "Open " + controller.getHeader());
                    if(optional.get() == ButtonType.OK) {                        
                        try {
                            //Lấy thành công
                            int noteId = controller.getId();
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
                            //Load lại Edit Scene và mở Edit Scene
                            
                        } catch (NoteServiceException ex) {
                            showAlert(Alert.AlertType.ERROR, ex.getMessage());
                        }
                    }
                });
                //Thêm Note Card vào layout
                noteCardLayout.getChildren().add(box);
            } catch (IOException ex) {
                System.err.println(ex);
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
                
                HBox box = controller.loadFXML(filePath, controller);
                controller.setData(shareNotes.get(i));
                //Xử lý khi nhấn vào note card
                box.setOnMouseClicked((MouseEvent event) -> {
                    //Tạo thông báo và mở note nếu chọn OK
                    Optional<ButtonType> optional = showAlert(Alert.AlertType.CONFIRMATION, 
                            "Open " + controller.getHeader());
                    if(optional.get() == ButtonType.OK) {                        
                        try {
                            //Lấy thành công
                            int noteId = controller.getId();
                            currentNote = shareNoteService.open(noteId, controller.getEditor());
                            if (!openedNotes.contains(currentNote)) {
                                openedNotes.add(currentNote);
                            }
                            //Load lại Edit Scene và mở Edit Scene
                            EditShareNoteController.open(myUser, (ShareNote) currentNote, openedNotes, stage);
                        } catch (NoteServiceException ex) {
                            showAlert(Alert.AlertType.ERROR, ex.getMessage());
                        }
                    }
                });
                //Thêm Note Card vào layout
                noteCardLayout.getChildren().add(box);
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }        
    }
    
    protected void initHomeScene(List<Note> recentlyNotes) {
        recentlyVisitedLayout.getChildren().clear();
        if (recentlyNotes.isEmpty()) {
            return;
        }
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "RecentlyNoteCardView.fxml";
        for (Note note: recentlyNotes) {
            try {
                RecentlyNoteCardController controller = new RecentlyNoteCardController();
                VBox box = controller.loadFXML(filePath, controller);
                controller.setData(note);
                box.setOnMouseClicked((event) -> {
                    Optional<ButtonType> optional = showAlert(Alert.AlertType.CONFIRMATION, 
                            "Open " + controller.getNote().getHeader());
                    if (optional.get() == ButtonType.OK) {
                        int noteId = controller.getNote().getId();
                        try {
                            Note selectedNote = noteService.open(noteId);
                            if (!openedNotes.contains(selectedNote)) {
                                openedNotes.add(selectedNote);
                            }
                            System.out.println(openedNotes);
                            if (note.isPubliced()) {
                                ShareNote selectedShareNote = shareNoteService.open(noteId, myUser.getUsername());
                                EditShareNoteController.open(myUser, selectedShareNote, openedNotes, stage);
                            } else {
                                EditNoteController.open(myUser, selectedNote, openedNotes, stage);
                            }
                        } catch (NoteServiceException ex) {
                        }
                    }
                });
                recentlyVisitedLayout.getChildren().add(box);
            } catch (IOException ex) {
            }
        }
    }
    
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
    
       
    protected void initImportExportScene(List<Note> notes) {
        //Clear ComboBox và thêm vào các header note trong list
        exportNoteComboBox.getItems().clear();
        if(notes.isEmpty()) {
            return;
        }
        for(Note note: notes) {
            exportNoteComboBox.getItems().add(note.getHeader());
        }
        //Clear ComboBox và thêm vào các định dạng cho phép
        exportFormatComboBox.getItems().clear();
        exportFormatComboBox.getItems().add("PDF");
        //Set header của note đang edit
        importNoteName.setText(currentNote.getHeader());
    }

    protected void initShareNoteScene(List<Note> notes, List<ShareNote> shareNotes) {
        
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
    
    protected void searchNote() {
        //Lấy thông tin cần search
        String searchText = searchNoteField.getText();
        //Tạo list mới để chứa các note hợp lệ
        List<Note> notes = new ArrayList<>();
        List<ShareNote> shareNotes = new ArrayList<>();
        //Thêm các note hợp lệ vào list
        for(Note newNote: myNotes) {
            if(newNote.getHeader().contains(searchText)) { 
                notes.add(newNote);
            } else {
                for(NoteFilter noteFilter: newNote.getFilters()) {
                    if(noteFilter.getFilter().contains(searchText)) {
                        notes.add(newNote);
                    }
                }
            }
        }
        for (ShareNote newShareNote: mySharedNotes) {
            if (newShareNote.getHeader().contains(searchText)) {
                shareNotes.add(newShareNote);
            } else {
                for (NoteFilter noteFilter: newShareNote.getFilters()) {
                    if (noteFilter.getFilter().contains(searchText)) {
                        shareNotes.add(newShareNote);
                    }
                }
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
                initMyNotesScene(myNotes, mySharedNotes);
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
                initMyNotesScene(myNotes, mySharedNotes);
            } catch (NoteServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });
    }

    protected void saveAccount() {
        errorEmailFieldLabel.setVisible(false);
        errorBirthdayFieldLabel.setVisible(false);
        errorNameFieldLabel.setVisible(false);
        errorPasswordFieldLabel.setVisible(false);
        //Lấy password
        if("".equals(passwordField.getText())) {
            errorPasswordFieldLabel.setVisible(true);
        }
        myUser.setPassword(passwordField.getText());
        //Lấy Email
        Email email = new Email();
        email.setAddress(emailAddressField.getText());
        if(!email.checkEmailAddress()) {
            errorEmailFieldLabel.setVisible(true);
        }
        myUser.setEmail(email);
        //Láy thông tin name
        if("".equals(nameField.getText())) {
            errorNameFieldLabel.setVisible(true);
        }
        myUser.setName(nameField.getText());
        //Lấy school
        myUser.setSchool(schoolField.getText());
        //Lấy thông tin về birth
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
        //Lấy gender
        if(genderMale.isSelected()) {
            myUser.setGender(User.Gender.MALE);
        } else if (genderFemale.isSelected()) {
            myUser.setGender(User.Gender.FEMALE);
        } else {
            myUser.setGender(User.Gender.OTHER);
        }
        //Kiểm tra xem có lỗi nào không
        if(errorNameFieldLabel.isVisible() || errorPasswordFieldLabel.isVisible() 
                || errorBirthdayFieldLabel.isVisible() || errorEmailFieldLabel.isVisible()) {
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

    protected void exportFile() {
        System.out.println("Hello");
    }

    protected void importFile() {
        System.out.println("hello");
    }

    protected void chooseFileToInput() {
        //Tạo FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose your file");
        //Thiết lập loại file được phép chọn
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("PDF Files", "*.pdf");
        fileChooser.getExtensionFilters().add(extensionFilter);
        //Show FileChooser
        File file = fileChooser.showOpenDialog(null);
        //Lấy đường dẫn của file được chọn
        if(file != null) {
            importFileName.setText(file.getPath());
        }
    }

    protected void sendNote() {
        //Lấy header được chọn từ ComboBox và lấy note tương ứng
        String selectedNoteHeader = chooseShareNoteComboBox.getSelectionModel().getSelectedItem();
        try { 
            //Lấy thành công
            Note selectedNote = new Note();
            for(Note note: myNotes) {
                if(note.getHeader().equals(selectedNoteHeader)) {
                    selectedNote = note;
                }
            }
            selectedNote = noteService.open(selectedNote.getId());
            //Lấy receiver Id
            String receiverUsename = chooseUserShareField.getText();
            //Tạo ShareNote mới để Share
            ShareNote.ShareType shareType;
            if(shareTypeReadOnly.isSelected()) {
                shareType = ShareNote.ShareType.READ_ONLY;
            } else {
                shareType = ShareNote.ShareType.CAN_EDIT;
            }
            shareNoteService.share(selectedNote, receiverUsename, shareType);
            //Thông báo
            showAlert(Alert.AlertType.INFORMATION, "Successfully share");
        } catch (NoteServiceException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }
 
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
        shareNoteButton.setStyle(unPressedStyle);
        shareNoteScene.setVisible(false);
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
        } else if (button == shareNoteButton) {
            shareNoteButton.setStyle(pressedStyle);
            shareNoteScene.setVisible(true);
        }
    }
    
    public static void open(User myUser, Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "DashboardView.fxml";
            
            DashboardController controller = new DashboardController();

            controller.setStage(stage);
            controller.setMyUser(myUser);
            controller.setCurrentNote(new Note());
            controller.setOpenedNotes(new ArrayList<>());
            controller.loadFXMLAndSetScene(filePath, controller);
            controller.init();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open dashboard");
        }
    }
    
    public static void open(User myUser, Note currentNote, List<Note> openedNotes, Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "DashboardView.fxml";
            
            DashboardController controller = new DashboardController();

            controller.setStage(stage);
            controller.setMyUser(myUser);
            controller.setOpenedNotes(openedNotes);
            controller.setCurrentNote(currentNote);
            controller.loadFXMLAndSetScene(filePath, controller);
            
            controller.init();
            
            
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open dashboard");
        }
    }
}