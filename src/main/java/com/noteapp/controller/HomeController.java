package com.noteapp.controller;

import static com.noteapp.controller.Controller.showAlert;
import com.noteapp.note.model.Note;
import com.noteapp.note.model.ShareNote;
import com.noteapp.note.service.NoteServiceException;
import com.noteapp.user.model.User;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 *
 * @author admin
 */
public class HomeController extends Controller {
    @FXML
    private FlowPane recentlyVisitedLayout;
    
    protected List<Note> recentlyNotes;
    protected User myUser;
    
    @Override
    public void init() {
        initServerService();
        initRecentlyVisitedLayout();
    }
    
    protected void initRecentlyVisitedLayout() {
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
                            if (note.isPubliced()) {
                                ShareNote selectedShareNote = shareNoteService.open(noteId, myUser.getUsername());
                                openEditView(myUser, selectedShareNote);
                            } else {
                                openEditView(myUser, selectedNote);
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
    
    protected void openEditView(User user, ShareNote shareNote) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "EditNoteView.fxml";
            
            EditShareNoteController controller = new EditShareNoteController();

            controller.setStage(stage);
            controller.setMyUser(user);
            controller.setMyNote(shareNote);
            controller.setMyShareNote(shareNote);
            controller.loadFXMLAndSetScene(filePath, controller);
            controller.init();
            controller.setOnAutoUpdate();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open view");
        }
    }
    
    protected void openEditView(User user, Note note) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "EditNoteView.fxml";
            
            EditNoteController controller = new EditNoteController();

            controller.setStage(stage);
            controller.setMyUser(user);
            controller.setMyNote(note);
            controller.loadFXMLAndSetScene(filePath, controller);
            controller.init();
            //Set scene cho stage và show
            
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open login");
        }
    }
}
