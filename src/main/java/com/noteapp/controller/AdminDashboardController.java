package com.noteapp.controller;

import com.noteapp.service.NoteAppService;
import com.noteapp.service.NoteAppServiceException;
import com.noteapp.user.dao.AdminDAO;
import com.noteapp.user.dao.UserDAO;
import com.noteapp.user.model.Admin;
import com.noteapp.user.service.AdminService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller cho view Dashboard của Admin
 * @author admin
 */
public class AdminDashboardController extends RequestServiceController implements Initable {
    @FXML
    private Button viewUsersButton;
    @FXML
    private TextField searchUserField;
    @FXML
    private VBox userCardLayout;
    @FXML
    private Button closeButton;
    @FXML
    private Button logoutButton;

    private Map<String, Boolean> lockedStatusOfUsers;
    private Admin myAdmin;

    public void setMyAdmin(Admin myAdmin) {
        this.myAdmin = myAdmin;
    }
    
    @Override
    public void init() {
        noteAppService = new NoteAppService();
        noteAppService.setAdminService(new AdminService(UserDAO.getInstance(), AdminDAO.getInstance()));
        initView();
        closeButton.setOnAction((ActionEvent event) -> {
            close();
        });
        logoutButton.setOnAction((ActionEvent event) -> {
            LoginController.open(stage);
        });
        viewUsersButton.setOnAction((ActionEvent event) -> {
            initView();
        });
        searchUserField.setOnAction((ActionEvent event) -> {
            searchUser();
        });
    }

    protected void initView() {
        try {
            lockedStatusOfUsers = noteAppService.getAdminService().getAllLockedStatus(myAdmin.getUsername());
            loadUsers(lockedStatusOfUsers);
        } catch (NoteAppServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }   
    }
    
    private void saveLockedStatus(String username, boolean lockedStatus) {
        try {
            noteAppService.getAdminService().updateLockedStatus(username, lockedStatus);
        } catch (NoteAppServiceException ex) {
            showAlert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }
    
    /**
     * Tải thông tin về các User và trạng thái Locked của họ vào
     * một danh sách có thể hiển thị trên màn hình
     * @param lockedStatusOfUsers Một Map lưu giữ trạng thái Locked của mỗi User
     * @see userCardLayout
     * @see UserItemController
     */
    protected void loadUsers(Map<String, Boolean> lockedStatusOfUsers) {
        userCardLayout.getChildren().clear();
        if (lockedStatusOfUsers.isEmpty()) {
            return;
        }
        String filePath = Controller.DEFAULT_FXML_RESOURCE + "UserItemView.fxml";
        //Load từng user vào UserItemController
        for (Map.Entry<String, Boolean> entry: lockedStatusOfUsers.entrySet()) {
            String username = entry.getKey();
            boolean isLocked = entry.getValue();
            try {
                UserItemController controller = new UserItemController();
                HBox box = controller.loadFXML(filePath);
                controller.setData(username, isLocked);
                //Thêm action cho nút đổi trạng thái lock
                controller.getChangeLockStatusButton().setOnAction((ActionEvent event) -> {
                    controller.changeLockedStatus();
                    String thisUsername = controller.getUsername();
                    boolean thisLockedStatus = controller.isLocked();
                    this.lockedStatusOfUsers.put(thisUsername, thisLockedStatus);                  
                    saveLockedStatus(thisUsername, thisLockedStatus);
                });
                userCardLayout.getChildren().add(box);
            } catch (IOException ex) {
                showAlert(Alert.AlertType.ERROR, "Can load user items");
            }
        }
    }
    
    /**
     * Thực hiện tìm kiếm user có username chứa chuỗi được nhập vào
     * {@link searchUserField} và load lại danh sách hiển thị những
     * user khớp với yêu cầu
     */
    protected void searchUser() {
        String searchText = searchUserField.getText();
        Map<String, Boolean> searchUsers = new HashMap<>();
        for (Map.Entry<String, Boolean> entry: lockedStatusOfUsers.entrySet()) {
            String username = entry.getKey();
            boolean isLocked = entry.getValue();
            if (username.contains(searchText)) {
                searchUsers.put(username, isLocked);
            }
        }
        loadUsers(searchUsers);
    }
    
    /**
     * Mở một giao diện Dashboard cho Admin
     * @param stage Stage được truyền vào để chứa giao diện này
     */
    public static void open(Admin myAdmin, Stage stage) {
        try {
            String filePath = Controller.DEFAULT_FXML_RESOURCE + "AdminDashboardView.fxml";
            AdminDashboardController controller = new AdminDashboardController();
            controller.setStage(stage);
            controller.setMyAdmin(myAdmin);
            controller.loadFXMLAndSetScene(filePath);
            controller.init();
            controller.showFXML();
        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Can't open admin dashboard");
        }
    }
}
