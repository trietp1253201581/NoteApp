/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.noteapp.controller;

import com.noteapp.note.model.SurveyBlock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author admin
 */
public class SurveyBlockController extends Controller {
    @FXML
    private Label blockHeader;
    @FXML
    private Label changeNotify;
    @FXML
    private VBox itemsLayout;
    @FXML
    private Button addItemButton;
    @FXML
    private Button deleteButton;
    
    private int noteId;
    private SurveyBlock surveyBlock;
    private List<SurveyBlock> otherEditors;
    private List<SurveyItemController> itemsController;
    
    @Override
    public void init() {
        noteId = -1;
        surveyBlock = new SurveyBlock();
        otherEditors = new ArrayList<>();
        itemsController = new ArrayList<>();
        
        addItemButton.setOnAction((ActionEvent event) -> {
            addItem();
        });
    }
    
    public void loadItems() {
        itemsController.clear();
        itemsLayout.getChildren().clear();
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "SurveyItemView.fxml";
        if (surveyBlock.getSurveyMap().isEmpty()) {
            return;
        }
        for (Map.Entry<String, Boolean> entry: surveyBlock.getSurveyMap().entrySet()) {
            String choice = entry.getKey();
            boolean isVoted = entry.getValue();
            int numVoted = 0;
            if (isVoted) {
                numVoted += 1;
            }
            try {             
                SurveyItemController controller = new SurveyItemController();
                HBox box = controller.loadFXML(filePath, controller);
                
                controller.init();
                controller.setChoice(choice);
                
                for (SurveyBlock otherEditor: otherEditors) {
                    boolean otherVoted = otherEditor.getSurveyMap().get(choice);
                    if (otherVoted) {
                        numVoted += 1;
                    }
                }
                controller.getVotedRatioButton().setSelected(isVoted);
                controller.setNum(numVoted);
                controller.getVotedRatioButton().setOnAction((ActionEvent event) -> {
                    boolean radioVoted = controller.getVotedRatioButton().isSelected();
                    controller.setVoted(radioVoted);
                    surveyBlock.getSurveyMap().put(choice, radioVoted);
                });
                List<String> others = new ArrayList<>();
                for (SurveyBlock otherBlock: otherEditors) {
                    if (otherBlock.getSurveyMap().get(choice)) {
                        others.add(otherBlock.getEditor());
                    }
                }
                controller.setOther(others);
                itemsController.add(controller);
                itemsLayout.getChildren().add(box);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }
    
    protected void addItem() {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setTitle("Add a choice to survey");
        inputDialog.setHeaderText("Input your new choice");
        //Lấy kết quả và xử lý
        Optional<String> confirm = inputDialog.showAndWait();
        confirm.ifPresent(newChoice -> {
            //Lấy tất cả các Note của myUser
            try {
                String filePath = Controller.DEFAULT_FXML_RESOURCE + "SurveyItemView.fxml";
                SurveyItemController controller = new SurveyItemController();
                HBox box = controller.loadFXML(filePath, controller);
                
                controller.init();
                controller.setChoice(newChoice);
                controller.getDeleteChoiceButton().setOnAction((ActionEvent event) -> {
                    String deletedChoice = controller.getChoice();
                    int deletedIdx = -1;
                    for (int i = 0; i < itemsController.size(); i++) {
                        if (deletedChoice.equals(itemsController.get(i).getChoice())) {
                            deletedIdx = i;
                            break;
                        }
                    }
                    itemsController.remove(deletedIdx);
                    itemsLayout.getChildren().remove(deletedIdx);
                    surveyBlock.getSurveyMap().remove(deletedChoice);
                });
                controller.getVotedRatioButton().setOnAction((ActionEvent event) -> {
                    boolean radioVoted = controller.getVotedRatioButton().isSelected();
                    controller.setVoted(radioVoted);
                });
                itemsController.add(controller);
                itemsLayout.getChildren().add(box);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }
    
    
    public String getHeader() {
        return blockHeader.getText();
    }
    
    public void setHeader(String header) {
        blockHeader.setText(header);
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public SurveyBlock getSurveyBlock() {
        return surveyBlock;
    }

    public void setSurveyBlock(SurveyBlock surveyBlock) {
        this.surveyBlock = surveyBlock;
    }

    public List<SurveyBlock> getOtherEditors() {
        return otherEditors;
    }

    public void setOtherEditors(List<SurveyBlock> otherEditors) {
        this.otherEditors = otherEditors;
    }
    
    public Button getDeleteButton() {
        return deleteButton;
    }
}
