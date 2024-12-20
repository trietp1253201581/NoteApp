package com.noteapp.note.dao;

import com.noteapp.common.dao.DAOException;
import com.noteapp.note.model.TextBlock;
import java.util.List;

/**
 * Cung cấp các phương thức thao tác với CSDL TextBlock
 * @author admin
 */
public interface ITextBlockDAO {
    
    /**
     * Lấy tất cả các phiên bản của một {@link TextBlock} (với các editor khác
     * nhau) đối với cùng một NoteBlock
     * @param blockId id của NoteBlock
     * @return Một List chứa các phiên bản chỉnh sửa bởi các editor khác nhau
     * đối với NoteBlock này
     * @throws DAOException Xảy ra khi có lỗi về kết nối và câu lệnh
     * @see com.noteapp.note.model.NoteBlock
     */
    List<TextBlock> getAll(int blockId) throws DAOException;
    
    /**
     * Tạo một {@link TextBlock} mới và lưu vào CSDL. Để có thể tạo được thành
     * công thì TextBlock này phải là một NoteBlock đã tồn tại và editor cũng phải
     * là một User đã tồn tại trong CSDL
     * @param newTextBlock TextBlock cần tạo
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh và khóa ngoài
     * @see com.noteapp.note.model.NoteBlock
     */
    void create(TextBlock newTextBlock) throws DAOException;
    
    /**
     * Cập nhật một {@link TextBlock} trong CSDL
     * @param textBlock TextBlock cần chỉnh sửa
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh
     */
    void update(TextBlock textBlock) throws DAOException;
}
