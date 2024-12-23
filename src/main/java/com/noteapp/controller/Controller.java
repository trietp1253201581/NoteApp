package com.noteapp.controller;

import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Class cung cấp các thuộc tính và phương thức chung của các controller
 * @author Nhóm 17
 */
public abstract class Controller {
    protected double posX, posY;
    protected Stage stage;
    protected Scene scene;
    
    public static final String DEFAULT_FXML_RESOURCE = "/com/noteapp/view/";
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    /**
     * Hiển thị một thông báo (Alert)
     * @param alertType Loại thông báo được hiển thị
     * @param text Thông điệp muốn hiển thị
     * @return Một {@link Optional} bao gồm kết quả trả về
     * @see Alert
     * @see Optional
     * @see ButtonType
     */
    public static Optional<ButtonType> showAlert(Alert.AlertType alertType, String text) {
        Alert alert = new Alert(alertType);
        alert.setTitle(String.valueOf(alertType));
        alert.setHeaderText(text);
        return alert.showAndWait();
    }
    
    /**
     * Thiết lập một scene có thể di chuyển trên màn hình
     */
    public void setSceneMoveable() {
        posX = 0;
        posY = 0;
        scene.setOnMousePressed((MouseEvent mouseEvent) -> {
            posX = mouseEvent.getSceneX();
            posY = mouseEvent.getSceneY();
        });       
        scene.setOnMouseDragged((MouseEvent mouseEvent) -> {
            stage.setX(mouseEvent.getScreenX() - posX);
            stage.setY(mouseEvent.getScreenY() - posY);
        });
    }
           
    protected void close() {
        Optional<ButtonType> optional = showAlert(Alert.AlertType.CONFIRMATION,
                "Close NoteLite?");
        if(optional.get() == ButtonType.OK) {
            System.exit(0);
        }
    }
    
    /**
     * Load một FXML file chứa các thành phần giao diện và thiết lập nó là
     * scene chính để hiển thị lên màn hình
     * @param filePath Đường dẫn tới FXML File
     * @throws IOException Xảy ra khi việc load file bị lỗi
     * @see FXMLLoader#load()
     */
    public void loadFXMLAndSetScene(String filePath) throws IOException {
        Parent root = this.loadFXML(filePath);
        scene = new Scene(root);
        setSceneMoveable();
        stage.setScene(scene);
    }
    
    /**
     * Load một FXML file chứa các thành phần giao diện và thiết lập nó là
     * một đối tượng container có thể hiển thị lên màn hình
     * @param <T> Một container có thể hiển thị lên màn hình, 
     * phải là con của {@link Parent}
     * @param filePath Đường dẫn tới FXML File
     * @throws IOException Xảy ra khi việc load file bị lỗi
     * @see FXMLLoader#load()
     */
    public <T extends Parent> T loadFXML(String filePath) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(filePath));
        loader.setController(this);
        T root = loader.load();
        return root;
    }
    
    public void showFXML() {
        stage.show();
    }
}
