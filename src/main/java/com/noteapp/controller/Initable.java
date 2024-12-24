package com.noteapp.controller;

/**
 * Yêu cầu các class kế thừa nó phải khởi tạo qua một phương thức
 * trước khi sử dụng
 * @author admin
 */
public interface Initable {
    /**
     * Phương thức khởi tạo các giá trị, thuộc tính cần thiết
     * và thiết lập các action event khi bấm vào các đối tượng
     * hiển thị trên view
     */
    public abstract void init();
}
