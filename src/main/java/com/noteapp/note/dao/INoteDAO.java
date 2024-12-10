package com.noteapp.note.dao;

import com.noteapp.common.dao.DAOException;
import com.noteapp.note.model.Note;
import java.util.List;

/**
 * Cung cấp các phương thức thao tác với CSDL Note
 * @author admin
 */
public interface INoteDAO {
    
    /**
     * Lấy tất cả các {@link Note} của một {@link User}
     * @param author username của User sở hữu Note
     * @return Một List chứa các Note của User này
     * @throws DAOException Xảy ra khi có lỗi về key hoặc câu lệnh, kết nối
     * @see Note
     */
    List<Note> getAll(String author) throws DAOException;
    
    /**
     * Lấy Note theo id cho trước
     * @param noteId id của note cần lấy
     * @return Note được lấy
     * @throws DAOException Xảy ra khi không tồn tại note có id này, hoặc
     * câu lệnh, kết nối bị lỗi
     * @see Note
     */
    Note get(int noteId) throws DAOException;
    
    /**
     * Tạo một note mới và lưu vào CSDL
     * @param newNote note cần tạo
     * @return Note được tạo thành công
     * @throws DAOException Nếu note không được tạo thành công do câu lệnh,
     * kết nối hoặc các ràng buộc về khóa ngoài trong CSDL
     * @see Note
     */
    Note create(Note newNote) throws DAOException;
    
    /**
     * Cập nhật một Note trong CSDL
     * @param note note được cập nhật
     * @throws DAOException Xảy ra khi có lỗi về câu lệnh, kết nối
     * @see Note
     */
    void update(Note note) throws DAOException;
    
    /**
     * Xóa một Note trong CSDL, lúc này do các mối quan hệ khóa ngoài
     * nên các thông tin liên quan trong CSDL NoteBlock, NoteFilter cũng sẽ
     * bị xóa
     * @param noteId id của Note cần xóa
     * @throws DAOException Xảy ra khi có lỗi về câu lệnh, kết nối
     * @see Note
     * @see INoteBlockDAO
     * @see INoteFilterDAO
     */
    void delete(int noteId) throws DAOException;
}