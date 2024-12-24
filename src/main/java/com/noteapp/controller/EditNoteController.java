package com.noteapp.controller;

import com.noteapp.service.NoteAppService;
import com.noteapp.service.NoteAppServiceException;
import com.noteapp.note.dao.NoteBlockDAO;
import com.noteapp.note.dao.NoteDAO;
import com.noteapp.note.dao.NoteFilterDAO;
import com.noteapp.note.dao.ShareNoteDAO;
import com.noteapp.note.dao.SurveyBlockDAO;
import com.noteapp.note.dao.TextBlockDAO;
import com.noteapp.note.model.Note;
import com.noteapp.note.model.NoteBlock;
import com.noteapp.note.model.NoteFilter;
import com.noteapp.note.model.ShareNote;
import com.noteapp.note.model.SurveyBlock;
import com.noteapp.note.model.TextBlock;
import com.noteapp.note.service.NoteService;
import com.noteapp.user.model.User;
import com.noteapp.note.service.ShareNoteService;
import com.noteapp.note.service.io.PdfIOService;
import com.noteapp.user.dao.UserDAO;
import com.noteapp.user.service.UserService;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/**
 * FXML Controller class cho trang Edit Note
 * @author Nhóm 17
 */
public class EditNoteController extends RequestServiceController implements Initable {
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
    @FXML
    protected Button importFileButton;
    @FXML
    protected Button exportFileButton;
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
        noteAppService = new NoteAppService();
        noteAppService.setUserService(new UserService(UserDAO.getInstance()));
        noteAppService.setNoteService(new NoteService(NoteDAO.getInstance(), NoteFilterDAO.getInstance(), 
                NoteBlockDAO.getInstance(), TextBlockDAO.getInstance(), SurveyBlockDAO.getInstance()));
        noteAppService.setShareNoteService(new ShareNoteService(ShareNoteDAO.getInstance(),  
                NoteDAO.getInstance(), NoteFilterDAO.getInstance(), 
                NoteBlockDAO.getInstance(), TextBlockDAO.getInstance(), SurveyBlockDAO.getInstance()));
        noteAppService.setFileIOService(new PdfIOService());
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
        importFileButton.setOnAction((ActionEvent event) -> {
            importFile();
        });
        exportFileButton.setOnAction((ActionEvent event) -> {
            exportFile();
        });
        addTextBlockButton.setOnAction((ActionEvent event) -> {
            TextBlock newBlock = new TextBlock();
            int maxSize = Integer.max(textBlockControllers.size(), surveyBlockControllers.size());
            newBlock.setOrder(maxSize + 1);
            newBlock.setHeader("Block " + newBlock.getOrder() + " of " + myNote.getHeader());
            newBlock.setEditor(myUser.getUsername());
            newBlock.setContent("Edit here");
            addBlock(newBlock);
        });
        addSurveyBlockButton.setOnAction((ActionEvent event) -> {
            SurveyBlock newBlock = new SurveyBlock();
            int maxSize = Integer.max(textBlockControllers.size(), surveyBlockControllers.size());
            newBlock.setOrder(maxSize + 1);
            newBlock.setHeader("SurveyBlock " + newBlock.getOrder() + " of " + myNote.getHeader());
            newBlock.setEditor(myUser.getUsername());
            addBlock(newBlock);
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
    
    private void openNote(Note needOpenNote) {
        if (needOpenNote.isPubliced()) {
            try {
                ShareNote needOpenShareNote = noteAppService.getShareNoteService().open(needOpenNote.getId(), myUser.getUsername());
                EditShareNoteController.open(myUser, needOpenShareNote, openedNotes, stage);
            } catch (NoteAppServiceException e) {
            }
        } else {
            EditNoteController.open(myUser, needOpenNote, openedNotes, stage);
        }
        
    }
    
    private void addOpenedNotesBox(Note openedNote) throws IOException {
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "OpenedNoteCardView.fxml";
        OpenedNoteCardController controller = new OpenedNoteCardController();
        HBox box = controller.loadFXML(filePath);
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
            openNote(needOpenNote);
        });
        openedNotesLayout.getChildren().add(box);
        
    }
    
    /**
     * Khởi tạo các Note đang được mở, được truyền vào từ dashboard
     * @see openNotesLayout
     */
    protected void initOpenedNotes() {
        openedNotesLayout.getChildren().clear();
        
        for (Note openedNote: openedNotes) {
            try {
                addOpenedNotesBox(openedNote);
            } catch (IOException ex) {
            }
        }
    }
    
    /**
     * Khởi tạo các block của Note này, các Block được truyền vào từ CSDL
     * @see #addBlock(TextBlock)
     * @see #addBlock(SurveyBlock) 
     */
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
    
    private boolean checkExistHeader(String newNoteHeader) {
        try { 
            List<Note> myNotes = noteAppService.getNoteService().getAll(myUser.getUsername());
            for(Note note: myNotes) {
                if(note.getHeader().equals(newNoteHeader)) {
                    showAlert(Alert.AlertType.ERROR, "This header exist");
                    return true;
                }
            }
        } catch (NoteAppServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
        return false;
    }
    
    protected void changeHeaderLabel() {
        //Mở dialog
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Change header for " + myNote.getHeader());
        inputDialog.setHeaderText("Input your new header");
        //Lấy kết quả và xử lý
        Optional<String> confirm = inputDialog.showAndWait();
        confirm.ifPresent(newNoteHeader -> {
            if (checkExistHeader(newNoteHeader)) return;
            noteHeaderLabel.setText(newNoteHeader);
        });
    }
    
    /**
     * Lưu Note hiện tại đang chỉnh sửa vào CSDL và thông báo lỗi nếu không lưu được.
     * Phương thức này sẽ reset dữ liệu của các block và lấy lại dữ liệu trên
     * trang edit hiện tại.
     */
    protected void saveMyNote() {
        myNote.setHeader(noteHeaderLabel.getText());
        myNote.setLastModifiedDate(Date.valueOf(LocalDate.now()));
        myNote.getBlocks().clear();
        for(int i=0; i<textBlockControllers.size(); i++) {
            TextBlock block = textBlockControllers.get(i).getTextBlock();
            block.setContent(textBlockControllers.get(i).getText());
            myNote.getBlocks().add(block);
        }
        for(int i=0; i<surveyBlockControllers.size(); i++) {
            SurveyBlock block = surveyBlockControllers.get(i).getSurveyBlock();     
            myNote.getBlocks().add(block);
        }
        System.out.println(myNote);
        try {
            noteAppService.getNoteService().save(myNote);
            showAlert(Alert.AlertType.INFORMATION, "Successfully save for " + myNote.getHeader());
        } catch (NoteAppServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    /**
     * Chia sẻ Note đang chỉnh sửa tới một User sẽ được nhập thông qua một Dialog,
     * cách thức chia sẻ là chỉ đọc (READ_ONLY) hay có thể edit (CAN_EDIT) cũng
     * sẽ được lựa chọn trong dialog này. Nếu user đang bị khóa tài khoản thì cũng
     * sẽ thông báo lỗi.
     */
    protected void shareMyNote() {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Share your note");
        dialog.setWidth(400);
        
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField receiverField = new TextField();
        receiverField.setPrefWidth(350);
        receiverField.setPromptText("Input username of receiver!");
        
        ComboBox<String> shareTypeComboBox = new ComboBox<>();
        shareTypeComboBox.getItems().addAll("Read only", "Can edit");
        shareTypeComboBox.setPrefWidth(350);
        
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
            boolean isExistReceiver = false;
            try {
                isExistReceiver = noteAppService.getUserService().isUser(receiver);
            } catch (NoteAppServiceException ex) {    
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
            if (!isExistReceiver) {
                showAlert(Alert.AlertType.ERROR, "Receiver is not exist!");
                return;
            }
            boolean isLocked = false;
            try {
                isLocked = noteAppService.getUserService().checkLocked(receiver);
            } catch (NoteAppServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
            if (isLocked) {
                showAlert(Alert.AlertType.ERROR, "Your account is locked!");
            }
            try {
                noteAppService.getShareNoteService().share(myNote, receiver, shareType);
                showAlert(Alert.AlertType.INFORMATION, "Successfully share!");
                ShareNote shareNote = noteAppService.getShareNoteService().open(myNote.getId(), myUser.getUsername());
                EditShareNoteController.open(myUser, shareNote, stage);
            } catch (NoteAppServiceException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
            }
        });
    }
    
    protected void addBlock(TextBlock newTextBlock) {
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "TextBlockView.fxml";
        try {
            TextBlockController controller = new TextBlockController();        
            
            VBox box = controller.loadFXML(filePath);
            controller.init();
            controller.setTextBlock(newTextBlock);
            controller.setNoteId(myNote.getId());
            controller.setText(newTextBlock.getContent());
            controller.setHeader(newTextBlock.getHeader());
            
            controller.getDeleteButton().setOnAction((ActionEvent event) -> {
                int idxToDelete = controller.getTextBlock().getOrder()-1;
                blocksLayout.getChildren().remove(idxToDelete);
                myNote.getBlocks().remove(idxToDelete);
                textBlockControllers.remove(idxToDelete);
            });
            
            controller.getUpButton().setOnAction((ActionEvent event) -> {
                int order = controller.getTextBlock().getOrder();
                if (order <= 1) return;
                swapOrder(order - 1, order);
                
                //Swap
                Node temp = blocksLayout.getChildren().get(order - 1);
                blocksLayout.getChildren().remove(order - 1);
                blocksLayout.getChildren().add(order - 2, temp);
            });
            
            controller.getDownButton().setOnAction((ActionEvent event) -> {
                int order = controller.getTextBlock().getOrder();
                if (order >= blocksLayout.getChildren().size()) return;
                swapOrder(order, order + 1);
                
                //Swap
                Node temp = blocksLayout.getChildren().get(order);
                blocksLayout.getChildren().remove(order);
                blocksLayout.getChildren().add(order - 1, temp);
            });
            
            controller.getBlockHeader().setOnMouseClicked((MouseEvent event) -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setHeaderText("Input your new header");
                
                dialog.showAndWait().ifPresent(newHeader -> {
                    controller.setHeader(newHeader);
                    controller.getTextBlock().setHeader(newHeader);
                });
                
            });
             
            blocksLayout.getChildren().add(box);
            textBlockControllers.add(controller);
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't load text block!");
        }
    }

    protected void addBlock(SurveyBlock newSurveyBlock) {
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "SurveyBlockView.fxml";
        try {
            SurveyBlockController controller = new SurveyBlockController();
            
            VBox box = controller.loadFXML(filePath);
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
            
            controller.getUpButton().setOnAction((ActionEvent event) -> {
                int order = controller.getSurveyBlock().getOrder();
                if (order <= 1) return;
                swapOrder(order - 1, order);
                
                //Swap
                Node temp = blocksLayout.getChildren().get(order - 1);
                blocksLayout.getChildren().remove(order - 1);
                blocksLayout.getChildren().add(order - 2, temp);
            });
            
            controller.getDownButton().setOnAction((ActionEvent event) -> {
                int order = controller.getSurveyBlock().getOrder();
                if (order >= blocksLayout.getChildren().size()) return;
                swapOrder(order, order + 1);
                
                //Swap
                Node temp = blocksLayout.getChildren().get(order);
                blocksLayout.getChildren().remove(order);
                blocksLayout.getChildren().add(order - 1, temp);
            });
            
            controller.getBlockHeader().setOnMouseClicked((MouseEvent event) -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setHeaderText("Input your new header");
                
                dialog.showAndWait().ifPresent(newHeader -> {
                    controller.setHeader(newHeader);
                    controller.getSurveyBlock().setHeader(newHeader);
                });
            });
             
            blocksLayout.getChildren().add(box);
            surveyBlockControllers.add(controller);
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't load survey block!");
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
    
    /**
     * Load các filter của note này và hiển thị lên màn hình
     * @param filters Các NoteFilter cần hiển thị
     * @param maxColEachRow Số lượng filter lớn nhất ở mỗi hàng
     */
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
                HBox hbox = controller.loadFXML(filePath);
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
            showAlert(Alert.AlertType.ERROR, "Can't load filter!");
        }
    }
    
    /**
     * Hàm trợ giúp việc thay đổi thứ tự giữa các block
     * @param firstOrder order của block thứ nhất
     * @param secondOrder order của block thứ hai
     */
    protected void swapOrder(int firstOrder, int secondOrder) {
        for (TextBlockController controller: textBlockControllers) {
            if (controller.getTextBlock().getOrder() == firstOrder) {
                controller.getTextBlock().setOrder(secondOrder);
            } else if (controller.getTextBlock().getOrder() == secondOrder) {
                controller.getTextBlock().setOrder(firstOrder);
            }
        }
        
        for (SurveyBlockController controller: surveyBlockControllers) {
            if (controller.getSurveyBlock().getOrder() == firstOrder) {
                controller.getSurveyBlock().setOrder(secondOrder);
            } else if (controller.getSurveyBlock().getOrder() == secondOrder) {
                controller.getSurveyBlock().setOrder(firstOrder);
            }
        }
    }
    
    protected void importFile() {
        //Tạo directory chooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose dir to export file");
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        try {
            Note importedNote = noteAppService.getFileIOService().importNote(file.getPath());
            myNote.setBlocks(importedNote.getBlocks());
            initView();
            showAlert(Alert.AlertType.INFORMATION, "Successfully import.");
        } catch (NoteAppServiceException | IOException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        } 
    }
    
    protected void exportFile() {
        //Tạo directory chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose dir to export file");
        File dir = directoryChooser.showDialog(stage);
        if (dir == null) {
            return;
        }
        //Export ra tên file tương ứng
        String pdfFileName = myNote.getHeader() + "_" + myNote.getAuthor() + ".pdf";
        try {
            noteAppService.getFileIOService().outputNote(dir + "\\" + pdfFileName, myNote);
            showAlert(Alert.AlertType.INFORMATION, "Successfully export.");
        } catch (NoteAppServiceException | IOException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        } 
    }

    /**
     * Mở một trang edit note
     * @param myUser User sở hữu Note
     * @param myNote Note được mở
     * @param stage Stage chứa trang edit này
     */
    public static void open(User myUser, Note myNote, Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "EditNoteView.fxml";
            
            EditNoteController controller = new EditNoteController();

            controller.setStage(stage);
            controller.setMyUser(myUser);
            controller.setMyNote(myNote);
            controller.setOpenedNotes(new ArrayList<>());
            controller.loadFXMLAndSetScene(filePath);
            controller.init();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open edit.");
        }
    } 
    
    /**
     * Mở một trang edit note với thông tin về các note đang được mở
     * @param myUser User sở hữu các note này
     * @param myNote Note đang được edit
     * @param openedNotes Các Note đang được mở
     * @param stage Stage chứa trang edit này
     */
    public static void open(User myUser, Note myNote, List<Note> openedNotes, Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "EditNoteView.fxml";
            
            EditNoteController controller = new EditNoteController();

            controller.setStage(stage);
            controller.setMyUser(myUser);
            controller.setMyNote(myNote);
            controller.setOpenedNotes(openedNotes);
            controller.loadFXMLAndSetScene(filePath);
            controller.init();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open edit.");
        }
    } 
}