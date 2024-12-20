package com.noteapp.controller;

import com.noteapp.note.model.Note;
import com.noteapp.note.model.NoteBlock;
import com.noteapp.note.model.NoteFilter;
import com.noteapp.note.model.ShareNote;
import com.noteapp.note.model.SurveyBlock;
import com.noteapp.note.model.TextBlock;
import com.noteapp.user.model.User;
import com.noteapp.note.service.NoteServiceException;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author admin
 */
public class EditNoteController extends Controller {
    //Các thuộc tính chung
    @FXML 
    protected Label noteHeaderLabel;
    @FXML
    protected Button homeMenuButton;
    @FXML
    protected Button editMenuButton;
    @FXML
    protected HBox editBox;
    //Các thuộc tính của edit Box
    @FXML
    protected Button saveNoteButton;
    @FXML
    protected Button openNoteButton;
    @FXML 
    protected Button addFilterButton;
    @FXML
    protected Button addTextBlockButton;
    @FXML
    protected Button addSurveyBlockButton;
    @FXML
    protected Button shareButton;
    //Các thuộc tính còn lại
    @FXML
    protected VBox blocksLayout;
    @FXML
    protected GridPane filterGridLayout;
    @FXML
    protected Button closeButton;
    @FXML
    protected HBox openedNotesLayout;
    
    protected User myUser;
    protected Note myNote;
    protected List<TextBlockController> textBlockControllers;
    protected List<SurveyBlockController> surveyBlockControllers;
    protected List<Note> openedNotes;

    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }
    
    public void setMyNote(Note myNote) {
        this.myNote = myNote;
    }

    public void setOpenedNotes(List<Note> openedNotes) {
        this.openedNotes = openedNotes;
    }
    
    @Override
    public void init() {
        initServerService();
        textBlockControllers = new ArrayList<>();
        surveyBlockControllers = new ArrayList<>();
        initView();
        closeButton.setOnAction((ActionEvent event) -> {
            close();
        });
        homeMenuButton.setOnAction((ActionEvent event) -> {
            DashboardController.open(myUser, myNote, openedNotes, stage);
        });
        noteHeaderLabel.setOnMouseClicked((MouseEvent event) -> {
            changeHeaderLabel();
        });
        saveNoteButton.setOnAction((ActionEvent event) -> {
            saveMyNote();
        });
        addFilterButton.setOnAction((ActionEvent event) -> {
            addFilter();
        });
        addTextBlockButton.setOnAction((ActionEvent event) -> {
            TextBlock newBlock = new TextBlock();
            newBlock.setOrder(textBlockControllers.size() + 1);
            newBlock.setHeader("Block " + newBlock.getOrder() + " of " + myNote.getHeader());
            newBlock.setEditor(myUser.getUsername());
            newBlock.setContent("Edit here");
            addBlock(newBlock);
        });
        addSurveyBlockButton.setOnAction((ActionEvent event) -> {
            showAlert(Alert.AlertType.ERROR, "Private note is not sưpported by this service!");
        });
        shareButton.setOnAction((ActionEvent event) -> {
            shareMyNote();
        });
    }
    
    protected void initView() {       
        noteHeaderLabel.setText(myNote.getHeader());
        initOpenedNotes();
        loadFilter(myNote.getFilters(), 8);
        initBlock();
    }
    
    protected void initOpenedNotes() {
        openedNotesLayout.getChildren().clear();
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "OpenedNoteCardView.fxml";
        for (Note openedNote: openedNotes) {
            try {
                OpenedNoteCardController controller = new OpenedNoteCardController();
                HBox box = controller.loadFXML(filePath, controller);
                controller.setNote(openedNote);
                controller.setHeader(openedNote.getHeader());
                controller.getRemoveNote().setOnMouseClicked((MouseEvent event) -> {
                    openedNotes.remove(controller.getNote());
                    initOpenedNotes();
                });
                box.getStyleClass().clear();
                if (openedNote.getId() == myNote.getId()) {
                    box.getStyleClass().add("focused-opened-note-card");
                } else {
                    box.getStyleClass().add("free-opened-note-card");
                }
                box.setOnMouseClicked((MouseEvent event) -> {
                    Note needOpenNote = controller.getNote();
                    if (needOpenNote.isPubliced()) {
                        try {
                            ShareNote needOpenShareNote = shareNoteService.open(needOpenNote.getId(), myUser.getUsername());
                            EditShareNoteController.open(myUser, needOpenShareNote, openedNotes, stage);
                        } catch (NoteServiceException e) {
                            e.printStackTrace();
                        }
                    } else {
                        EditNoteController.open(myUser, needOpenNote, openedNotes, stage);
                    }
                });
                openedNotesLayout.getChildren().add(box);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    protected void initBlock() {
        blocksLayout.getChildren().clear();
        List<NoteBlock> blocks = myNote.getBlocks();
        for(int i=0; i<blocks.size(); i++) {
            if(blocks.get(i).getBlockType() == NoteBlock.BlockType.TEXT) {
                addBlock((TextBlock) blocks.get(i));
            } else {
                addBlock((SurveyBlock) blocks.get(i));
            }
        }
    }
    
    @Override
    protected void close() {
        //saveMyNote();
        super.close();
    }
    
    protected void changeHeaderLabel() {
        //Mở dialog
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Change header for " + myNote.getHeader());
        inputDialog.setHeaderText("Input your new header");
        //Lấy kết quả và xử lý
        Optional<String> confirm = inputDialog.showAndWait();
        confirm.ifPresent(newNoteHeader -> {
            //Lấy tất cả các Note của myUser
            try { 
                List<Note> myNotes = noteService.getAll(myUser.getUsername());
                for(Note note: myNotes) {
                    if(note.getHeader().equals(newNoteHeader)) {
                        showAlert(Alert.AlertType.ERROR, "This header exist");
                        return;
                    }
                }
                //Thiết lập note name vừa nhập cho Label   
                noteHeaderLabel.setText(newNoteHeader);
            } catch (NoteServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });
    }
    
    protected void saveMyNote() {
        myNote.setHeader(noteHeaderLabel.getText());
        myNote.setLastModifiedDate(Date.valueOf(LocalDate.now()));
        myNote.getBlocks().clear();
        for(int i=0; i<textBlockControllers.size(); i++) {
            TextBlock block = textBlockControllers.get(i).getTextBlock();
            block.setContent(textBlockControllers.get(i).getTextFromView());
            myNote.getBlocks().add(block);
        }
        for(int i=0; i<surveyBlockControllers.size(); i++) {
            SurveyBlock block = surveyBlockControllers.get(i).getSurveyBlock();
            
            myNote.getBlocks().add(block);
        }
        try {
            noteService.save(myNote);
            showAlert(Alert.AlertType.INFORMATION, "Successfully save for " + myNote.getHeader());
        } catch (NoteServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    protected void shareMyNote() {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Share your note");
        
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField receiverField = new TextField();
        receiverField.setPromptText("Input username of receiver!");
        
        ComboBox<String> shareTypeComboBox = new ComboBox<>();
        shareTypeComboBox.getItems().addAll("Read only", "Can edit");
        
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(receiverField, 0, 0);
        grid.add(shareTypeComboBox, 0, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            List<String> results = new ArrayList<>();
            if (dialogButton == ButtonType.OK) {
                results.add(receiverField.getText());
                results.add(shareTypeComboBox.getSelectionModel().getSelectedItem());
            }
            return results;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            if (result.size() <= 1) return;
            ShareNote.ShareType shareType;
            if (result.get(1).equals("Can edit")) {
                shareType = ShareNote.ShareType.CAN_EDIT;
            } else {
                shareType = ShareNote.ShareType.READ_ONLY;
            }
            String receiver = result.get(0);
            try {
                shareNoteService.share(myNote, receiver, shareType);
                showAlert(Alert.AlertType.INFORMATION, "Successfully share!");
                ShareNote shareNote = shareNoteService.open(myNote.getId(), myUser.getUsername());
                EditShareNoteController.open(myUser, shareNote, stage);
            } catch (NoteServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });
    }
    
    protected void addBlock(TextBlock newTextBlock) {
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "TextBlockView.fxml";
        try {
            TextBlockController controller = new TextBlockController();        
            
            VBox box = controller.loadFXML(filePath, controller);
            controller.init();
            controller.setTextBlock(newTextBlock);
            controller.setNoteId(myNote.getId());
            controller.setTextForView(newTextBlock.getContent());
            controller.setHeader(newTextBlock.getHeader());
            
            controller.getDeleteButton().setOnAction((ActionEvent event) -> {
                int idxToDelete = controller.getTextBlock().getOrder()-1;
                blocksLayout.getChildren().remove(idxToDelete);
                myNote.getBlocks().remove(idxToDelete);
                textBlockControllers.remove(idxToDelete);
            });
             
            blocksLayout.getChildren().add(box);
            textBlockControllers.add(controller);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    protected void addBlock(SurveyBlock newSurveyBlock) {
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "SurveyBlockView.fxml";
        try {
            SurveyBlockController controller = new SurveyBlockController();
            
            VBox box = controller.loadFXML(filePath, controller);
            controller.init();
            controller.setSurveyBlock(newSurveyBlock);
            controller.setNoteId(myNote.getId());
            controller.setHeader(newSurveyBlock.getHeader());
            controller.loadItems();
            
            controller.getDeleteButton().setOnAction((ActionEvent event) -> {
                int idxToDelete = controller.getSurveyBlock().getOrder()-1;
                blocksLayout.getChildren().remove(idxToDelete);
                myNote.getBlocks().remove(idxToDelete);
                surveyBlockControllers.remove(idxToDelete);
            });
             
            blocksLayout.getChildren().add(box);
            surveyBlockControllers.add(controller);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    protected void addFilter() {
        //Hiện Dialog để nhập filter mới
        TextInputDialog inputDialog = new TextInputDialog();        
        inputDialog.setTitle("Add filter for " + myNote.getHeader());
        inputDialog.setHeaderText("Enter your new filter");   
        //Lấy kết quả
        Optional<String> confirm = inputDialog.showAndWait();
        //Xử lý kết quả khi nhấn OK
        confirm.ifPresent(newFilterStr -> {
            NoteFilter newFilter = new NoteFilter(newFilterStr);
            //Thiết lập và add tất cả các filter cũ     
            if(myNote.getFilters().contains(newFilter)) {
                //Nếu filter đã tồn tại thì thông báo lỗi
                showAlert(Alert.AlertType.ERROR, "Exist Filter");
            } else {
                //Thêm filter vào list
                myNote.getFilters().add(newFilter);
                //Load lại filter GUI
                loadFilter(myNote.getFilters(), 8);
            }
        }); 
    }
    
    protected void loadFilter(List<NoteFilter> filters, int maxColEachRow) {
        int column = 0;
        int row = 0;
        //Làm sạch filter layout
        filterGridLayout.getChildren().clear();
        if(filters.isEmpty()) {
            return;
        }
        //Thiết lập khoảng cách giữa các filter
        filterGridLayout.setHgap(8);
        filterGridLayout.setVgap(8);
        //Thiết lập filter layout
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "NoteFiltersView.fxml";
        try {
            for(int i = 0; i < filters.size(); i++) {
                NoteFiltersController controller = new NoteFiltersController();
                HBox hbox = controller.loadFXML(filePath, controller);
                //Thiết lập dữ liệu cho filter
                controller.setData(filters.get(i).getFilter());
                controller.getRemoveFilterView().setOnMouseClicked(event -> {
                    myNote.getFilters().remove(new NoteFilter(controller.getFilter()));
                    loadFilter(myNote.getFilters(), 8);
                });
                //Chuyển hàng
                if(column == maxColEachRow){
                    column = 0;
                    row++;
                }
                //Thêm filter vừa tạo vào layout
                filterGridLayout.add(hbox, column++, row);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void open(User myUser, Note myNote, Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "EditNoteView.fxml";
            
            EditNoteController controller = new EditNoteController();

            controller.setStage(stage);
            controller.setMyUser(myUser);
            controller.setMyNote(myNote);
            controller.setOpenedNotes(new ArrayList<>());
            controller.loadFXMLAndSetScene(filePath, controller);
            controller.init();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open edit.");
        }
    } 
    
    public static void open(User myUser, Note myNote, List<Note> openedNotes, Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "EditNoteView.fxml";
            
            EditNoteController controller = new EditNoteController();

            controller.setStage(stage);
            controller.setMyUser(myUser);
            controller.setMyNote(myNote);
            controller.setOpenedNotes(openedNotes);
            controller.loadFXMLAndSetScene(filePath, controller);
            controller.init();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open edit.");
        }
    } 
}