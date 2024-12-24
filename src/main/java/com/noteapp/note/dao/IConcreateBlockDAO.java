package com.noteapp.note.dao;

import com.noteapp.dao.DAOException;
import com.noteapp.note.model.NoteBlock;
import java.util.List;

/**
 * Cung cấp các phương thức thao tác với CSDL của các noteblock với kiểu xác định
 * @author Nhóm 17
 * @param <T> Kiểu của NoteBlock
 */
public interface IConcreateBlockDAO<T extends NoteBlock> {
    /**
     * Lấy tất cả các phiên bản của một {@link NoteBlock} dạng T (với các editor khác
     * nhau) đối với cùng một NoteBlock
     * @param blockId id của NoteBlock
     * @return Một List chứa các phiên bản chỉnh sửa bởi các editor khác nhau
     * đối với NoteBlock này
     * @throws DAOException Xảy ra khi có lỗi về kết nối và câu lệnh
     * @see com.noteapp.note.model.NoteBlock
     */
    List<T> getAll(int blockId) throws DAOException;
    
    /**
     * Tạo một {@link NoteBlock} dạng T dạng T mới và lưu vào CSDL. Để có thể tạo được thành
     * công thì {@link NoteBlock} dạng T này phải là một NoteBlock đã tồn tại và editor cũng phải
     * là một User đã tồn tại trong CSDL
     * @param newBlock {@link NoteBlock} dạng T cần tạo
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh và khóa ngoài
     * @see com.noteapp.note.model.NoteBlock
     */
    void create(T newBlock) throws DAOException;
    
    /**
     * Cập nhật một {@link NoteBlock} dạng T trong CSDL
     * @param block {@link NoteBlock} dạng T cần chỉnh sửa
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh
     */
    void update(T block) throws DAOException;
}
