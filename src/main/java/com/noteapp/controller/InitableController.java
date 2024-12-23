package com.noteapp.controller;

/**
 * Một class đại diện cho các controller bắt buộc phải init trước khi
 * được hiển thị
 * @author Nhóm 17
 */
public abstract class InitableController extends Controller {
    
    /**
     * Phương thức khởi tạo các giá trị, thuộc tính cần thiết
     * và thiết lập các action event khi bấm vào các đối tượng
     * hiển thị trên view
     */
    public abstract void init();
}
