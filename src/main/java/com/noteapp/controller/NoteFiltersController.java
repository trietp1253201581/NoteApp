package com.noteapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class cho các Note filter
 *
 * @author Nhóm 17
 * @see EditNoteController
 */
public class NoteFiltersController extends Controller {
    //Thuộc tính FXML
    @FXML
    private Label filterLabel;
    @FXML
    private ImageView removeFilterView;
    
    /**
     * Thiết lập dữ liệu cho filter
     * @param filter String chứa tên filter
     */
    public void setData(String filter){
        this.filterLabel.setText(filter);
    }   
    
    /**
     * Lấy nút remove filter
     * @return Nút Remove Filter
     */
    public ImageView getRemoveFilterView() {
        return removeFilterView;
    }
    
    /**
     * Lấy tên filter
     * @return tên của filter
     */
    public String getFilter() {
        return filterLabel.getText();
    }
}