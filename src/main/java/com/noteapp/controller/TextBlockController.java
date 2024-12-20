package com.noteapp.controller;

import com.noteapp.note.model.TextBlock;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 *
 * @author admin
 */
public class TextBlockController extends Controller {
    @FXML
    private Label viewText;
    @FXML
    private TextArea editableText;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button setToDefaultButton;
    @FXML
    private ComboBox<String> otherEditComboBox;
    @FXML
    private Label blockHeader;
    @FXML
    private Button returnToYoursButton;
    @FXML
    private Button switchToOtherButton;
    @FXML
    private Label changeNotify;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;
    
    private int noteId;
    private TextBlock textBlock;
    private boolean isEditing;
    private List<TextBlock> otherEditors;
    
    @Override
    public void init() {
        noteId = -1;
        textBlock = new TextBlock();
        isEditing = false;
        
        editButton.setOnAction((ActionEvent event) -> {
            setEditable(true);
            switchToEditableText();
        });
        
        saveButton.setOnAction((ActionEvent event) -> {
            setEditable(false);
            switchToViewText();
        });
        returnToYoursButton.setOnAction((ActionEvent event) -> {
            returnToYourContent();
        });
        otherEditComboBox.setPromptText("Other edit by");
        changeNotify.setText("");
    }
            
    public void initOtherEditComboBox() {
        otherEditComboBox.getItems().clear();
        otherEditComboBox.setPromptText("Other edit by");
        if(otherEditors.isEmpty()) {
            return;
        }
        for(TextBlock otherEditor: otherEditors) {
            otherEditComboBox.getItems().add(otherEditor.getEditor());
        }
    }
    
    public void setHeader(String header) {
        blockHeader.setText(header);
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public void setTextBlock(TextBlock textBlock) {
        this.textBlock = textBlock;
    }

    public void setIsEditing(boolean isEditing) {
        this.isEditing = isEditing;
    }
    
    public void setOtherEditors(List<TextBlock> otherEditors) {
        this.otherEditors = otherEditors;
    }

    public void updateOtherEditors(List<TextBlock> otherEditors) {
        List<String> hadModifiedEditor = new ArrayList<>();
        for (TextBlock oldBlock: this.otherEditors) {
            for (TextBlock newBlock: otherEditors) {
                boolean sameEditor = oldBlock.getEditor().equals(newBlock.getEditor());
                boolean sameContent = oldBlock.getContent().equals(newBlock.getContent());
                if (sameEditor && (!sameContent)) {
                    hadModifiedEditor.add(newBlock.getEditor());
                }
            }
        }

        setChangeNotify(hadModifiedEditor);
        this.otherEditors = otherEditors;
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getSetToDefaultButton() {
        return setToDefaultButton;
    }

    public Button getUpButton() {
        return upButton;
    }

    public Button getDownButton() {
        return downButton;
    }

    public ComboBox<String> getOtherEditComboBox() {
        return otherEditComboBox;
    }

    public Button getReturnToYoursButton() {
        return returnToYoursButton;
    }

    public Button getSwitchToOtherButton() {
        return switchToOtherButton;
    }

    public int getNoteId() {
        return noteId;
    }

    public TextBlock getTextBlock() {
        return textBlock;
    }

    public boolean isIsEditing() {
        return isEditing;
    }

    public List<TextBlock> getOtherEditors() {
        return otherEditors;
    }
    
    public String getOtherEditor() {
        return otherEditComboBox.getSelectionModel().getSelectedItem();
    }
    
    public String getTextFromView() {
        return viewText.getText();
    }
    
    public String getTextFromArea() {
        return editableText.getText();
    }
    
    public void setTextForView(String text) {
        viewText.setText(text);
    }
    
    public void setTextForArea(String text) {
        editableText.setText(text);
    }
    
    public void setEditable(boolean editable) {
        viewText.setVisible(!editable);
        editableText.setVisible(editable);
        isEditing = editable;
    }
    
    public void switchToEditableText() {        
        editableText.setText(viewText.getText());
        editableText.setPrefHeight(viewText.getPrefHeight());
        editableText.setPrefRowCount(viewText.getText().split("\n").length);
    }
    
    public void switchToViewText() {
        viewText.setText(editableText.getText());
        viewText.setPrefHeight(Region.USE_COMPUTED_SIZE);
    }
    
    public void returnToYourContent() {
        editableText.setText(textBlock.getContent());
        switchToViewText();
    }
    
    public void setChangeNotify(List<String> hadModifiedEditors) {
        if(hadModifiedEditors.isEmpty()) {
            changeNotify.setText("");
            return;
        }
        String notify = "";
        for(String str: hadModifiedEditors) {
            notify += str + ", ";
        }
        notify += "has modified their editor";
        changeNotify.setText(notify);
        changeNotify.setPrefWidth(Region.USE_COMPUTED_SIZE);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.textBlock);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TextBlockController other = (TextBlockController) obj;
        return Objects.equals(this.textBlock, other.textBlock);
    }
}