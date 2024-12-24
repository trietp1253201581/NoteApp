package com.noteapp.controller;

import com.noteapp.service.NoteAppServiceException;
import static com.noteapp.controller.Controller.showAlert;
import com.noteapp.note.model.Note;
import com.noteapp.note.model.NoteBlock;
import com.noteapp.note.model.ShareNote;
import com.noteapp.note.model.SurveyBlock;
import com.noteapp.note.model.TextBlock;
import com.noteapp.user.model.User;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Controller cho trang edit một note được chia sẻ giữa nhiều người
 * @author Nhóm 17
 */
public class EditShareNoteController extends EditNoteController {
    private Map<Integer, List<TextBlock>> othersTextBlockById;
    private Map<Integer, List<SurveyBlock>> othersSurveyBlockById;
   
    private ShareNote myShareNote;
    private Timer updateTimer;
    private TimerTask updateTimerTask;
    
    private boolean needReload;
    
    public void setMyShareNote(ShareNote myShareNote) {
        this.myShareNote = myShareNote;
    }
    
    @Override
    public void init() {
        needReload = false;
        super.init();
    }
    
    @Override 
    protected void initView() {
        noteHeaderLabel.setText(myShareNote.getHeader());
        initOpenedNotes();
        loadFilter(myNote.getFilters(), 8);
        this.initBlock();
    }
    
    @Override
    protected void initBlock() {
        blocksLayout.getChildren().clear();
        textBlockControllers.clear();
        surveyBlockControllers.clear();
        List<NoteBlock> blocks = myShareNote.getBlocks();
        Map<Integer, List<NoteBlock>> otherEditorBlocks = myShareNote.getOtherEditorBlocks();
        othersTextBlockById = new HashMap<>();
        othersSurveyBlockById = new HashMap<>();
        
        getBlocksById(otherEditorBlocks);
        
        for(int i=0; i<blocks.size(); i++) {
            if(blocks.get(i).getBlockType() == NoteBlock.BlockType.TEXT) {
                this.addBlock((TextBlock) blocks.get(i));
            } else {
                this.addBlock((SurveyBlock) blocks.get(i));
            }
        }
    }
    
    /**
     * Load lại toàn bộ dữ liệu trên trang edit
     */
    protected void reload() {
        updateTimerTask.cancel();
        updateTimer.cancel();
        initBlock();
        needReload = false;
        setOnAutoUpdate();
    }
    
    protected void autoUpdate() throws NoteAppServiceException {
        if (needReload) {
            Optional<ButtonType> btnType = showAlert(Alert.AlertType.WARNING, "You need to reload.");
            reload();
        }
        myShareNote = noteAppService.getShareNoteService().open(myShareNote.getId(), myShareNote.getEditor());
        noteHeaderLabel.setText(myShareNote.getHeader());
        loadFilter(myShareNote.getFilters(), 8);
        Map<Integer, List<NoteBlock>> otherEditorBlocks = myShareNote.getOtherEditorBlocks();
        getBlocksById(otherEditorBlocks);
        updateTextBlock();
        updateSurveyBlock();
    }
    
    /**
     * Cài đặt trang edit tự động cập nhật dữ liệu về các block được chỉnh sửa
     * bởi các user khác. Trong trường hợp thứ tự của các block bị thay đổi, hoặc
     * header của một block nào đó bị thay đổi, ứng dụng sẽ yêu cầu User reload lại
     * trang edit này.
     * @see #reload()
     * @see #updateTextBlock()
     * @see #updateSurveyBlock() 
     */
    public void setOnAutoUpdate() {
        updateTimer = new Timer();
        updateTimerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        autoUpdate();
                        
                    } catch (NoteAppServiceException ex) {
                        showAlert(Alert.AlertType.WARNING, "Can't update!");
                    }
                });
            }
        };
        updateTimer.scheduleAtFixedRate(updateTimerTask, 2000, 4000);
    }
    
    @Override
    protected void addBlock(TextBlock newTextBlock) {
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "TextBlockView.fxml";
        try {
            int blockId = newTextBlock.getId();
            TextBlockController controller = new TextBlockController();
            
            VBox box = controller.loadFXML(filePath);
            controller.init();
            controller.setTextBlock(newTextBlock);
            if (othersTextBlockById.containsKey(blockId)) {
                controller.setOtherEditors(othersTextBlockById.get(blockId));
            }
            controller.setNoteId(myNote.getId());
            controller.setText(newTextBlock.getContent());
            controller.setHeader(newTextBlock.getHeader());
            controller.initOtherEditComboBox();
            
            controller.getDeleteButton().setOnAction((ActionEvent event) -> {
                int idxToDelete = controller.getTextBlock().getOrder()-1;
                blocksLayout.getChildren().remove(idxToDelete);
                myNote.getBlocks().remove(idxToDelete);
                textBlockControllers.remove(idxToDelete);
            });
            controller.getSwitchToOtherButton().setOnAction((ActionEvent event) -> {
                String otherEditor = controller.getOtherEditor();
                TextBlock otherTextBlock = new TextBlock();
                for (TextBlock block: othersTextBlockById.get(blockId)) {
                    if (block.getEditor().equals(otherEditor)) {
                        otherTextBlock = block;
                        break;
                    }
                }
                controller.setText(otherTextBlock.getContent());
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
    
    @Override
    protected void addBlock(SurveyBlock newSurveyBlock) {
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "SurveyBlockView.fxml";
        try {
            int blockId = newSurveyBlock.getId();
            SurveyBlockController controller = new SurveyBlockController();
            
            VBox box = controller.loadFXML(filePath);
            controller.init();
            controller.setSurveyBlock(newSurveyBlock);
            if (othersSurveyBlockById.containsKey(blockId)) {
                controller.setOtherEditors(othersSurveyBlockById.get(blockId));
            }
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
    
    @Override
    protected void saveMyNote() {
        myShareNote.setHeader(noteHeaderLabel.getText());
        myShareNote.setLastModifiedDate(Date.valueOf(LocalDate.now()));
        myShareNote.getBlocks().clear();
        for(int i=0; i<textBlockControllers.size(); i++) {
            TextBlock block = textBlockControllers.get(i).getTextBlock();
            block.setContent(textBlockControllers.get(i).getText());
            myShareNote.getBlocks().add(block);
        }
        for(int i=0; i<surveyBlockControllers.size(); i++) {
            SurveyBlock block = surveyBlockControllers.get(i).getSurveyBlock();
            
            myShareNote.getBlocks().add(block);
        }
        try {
            noteAppService.getNoteService().save(myShareNote);
            showAlert(Alert.AlertType.INFORMATION, "Successfully save for " + myShareNote.getHeader());
        } catch (NoteAppServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }
    
    /**
     * Nhóm lại các block theo dạng text hay survey có cùng Id vào cùng 
     * một List để dễ dàng lấy các phiên bản
     * được chỉnh sửa bởi các editor khác của một Block nào đó
     * @param otherEditorBlocks Một danh sách các Block được chỉnh sửa bởi
     * các editor khác đã được nhóm theo id
     */
    protected void getBlocksById(Map<Integer, List<NoteBlock>> otherEditorBlocks) {
        othersTextBlockById = new HashMap<>();
        othersSurveyBlockById = new HashMap<>();
        
        for(Map.Entry<Integer, List<NoteBlock>> entry: otherEditorBlocks.entrySet()) {
            int blockId = entry.getKey();
            List<NoteBlock> others = entry.getValue();
            
            for(int i = 0; i < others.size(); i++) {
                if(others.get(i).getBlockType() == NoteBlock.BlockType.TEXT) {
                    if (!othersTextBlockById.containsKey(blockId)) {
                        othersTextBlockById.put(blockId, new ArrayList<>());
                    }
                    othersTextBlockById.get(blockId).add((TextBlock) others.get(i));
                } else {
                    if (!othersSurveyBlockById.containsKey(blockId)) {
                        othersSurveyBlockById.put(blockId, new ArrayList<>());
                    }
                    othersSurveyBlockById.get(blockId).add((SurveyBlock) others.get(i));
                }
            }
        }
    }
    
    protected void updateTextBlock() {
        for (int i = 0; i < textBlockControllers.size(); i++) {
            TextBlock thisBlock = textBlockControllers.get(i).getTextBlock();
            List<TextBlock> otherEditors = othersTextBlockById.get(thisBlock.getId());
            for (TextBlock otherEditor: otherEditors) {
                if (!otherEditor.getHeader().equals(thisBlock.getHeader())) {
                    needReload = true;
                    break;
                } 
                if (otherEditor.getOrder() != thisBlock.getOrder()) {
                    needReload = true;
                    break;
                }
            }

            textBlockControllers.get(i).updateOtherEditors(otherEditors);
            textBlockControllers.get(i).initOtherEditComboBox();
        }
    }
    
    protected void updateSurveyBlock() {
        for (int i = 0; i < surveyBlockControllers.size(); i++) {
            SurveyBlock thisBlock = surveyBlockControllers.get(i).getSurveyBlock();
            List<SurveyBlock> otherEditors = othersSurveyBlockById.get(thisBlock.getId());
            for (SurveyBlock otherEditor: otherEditors) {
                if (!otherEditor.getHeader().equals(thisBlock.getHeader())) {
                    needReload = true;
                    break;
                } 
                if (otherEditor.getOrder() != thisBlock.getOrder()) {
                    needReload = true;
                    break;
                }
            }
            surveyBlockControllers.get(i).setOtherEditors(otherEditors);
            surveyBlockControllers.get(i).loadItems();
        }
    }
    
    protected NoteBlock getBlock(int order) {
        for (TextBlockController textBlockController: textBlockControllers) {
            if (textBlockController.getTextBlock().getOrder() == order) {
                return textBlockController.getTextBlock();
            }
        }
        for (SurveyBlockController surveyBlockController: surveyBlockControllers) {
            if (surveyBlockController.getSurveyBlock().getOrder() == order) {
                return surveyBlockController.getSurveyBlock();
            }
        }
        return new NoteBlock();
    }
    
    @Override
    protected void exportFile() {
        //Tạo directory chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose dir to export file");
        File dir = directoryChooser.showDialog(stage);
        if (dir == null) {
            return;
        }
        //Export ra tên file tương ứng
        String pdfFileName = myNote.getHeader() + "_" + myShareNote.getAuthor() + ".pdf";
        try {
            noteAppService.getFileIOService().outputNote(dir + "\\" + pdfFileName, myShareNote);
            showAlert(Alert.AlertType.INFORMATION, "Successfully export.");
        } catch (NoteAppServiceException | IOException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        } 
    }
    
    /**
     * Mở một trang edit cho một note được chia sẻ giữa nhiều người
     * @param myUser User đang trong phiên đăng nhập
     * @param myShareNote Một Note được mở và được chia sẻ tới User này
     * @param stage Stage chứa trang edit này
     */
    public static void open(User myUser, ShareNote myShareNote, Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "EditNoteView.fxml";
            EditShareNoteController controller = new EditShareNoteController();
            controller.setStage(stage);
            controller.setMyUser(myUser);
            controller.setMyNote(myShareNote);
            controller.setMyShareNote(myShareNote);
            controller.setOpenedNotes(new ArrayList<>());
            controller.loadFXMLAndSetScene(filePath);
            controller.init();
            controller.setOnAutoUpdate();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open view");
        }
    }
    
    /**
     * Mở một trang edit cho một note được chia sẻ giữa nhiều người
     * @param myUser User đang trong phiên đăng nhập
     * @param myShareNote Một Note được mở và được chia sẻ tới User này
     * @param openedNotes Các Note đang được mở hiện tại
     * @param stage Stage chứa trang edit này
     */
    public static void open(User myUser, ShareNote myShareNote, List<Note> openedNotes, Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "EditNoteView.fxml";        
            EditShareNoteController controller = new EditShareNoteController();

            controller.setStage(stage);
            controller.setMyUser(myUser);
            controller.setMyNote(myShareNote);
            controller.setMyShareNote(myShareNote);
            controller.setOpenedNotes(openedNotes);
            controller.loadFXMLAndSetScene(filePath);
            controller.init();
            controller.setOnAutoUpdate();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open edit.");
        }
    } 
}
