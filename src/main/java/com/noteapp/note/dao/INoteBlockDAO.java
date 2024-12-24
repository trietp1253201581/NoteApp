package com.noteapp.note.dao;

import com.noteapp.dao.DAOException;
import com.noteapp.note.model.NoteBlock;
import java.util.List;

/**
 * Cung cấp các phương thức thao tác với CSDL NoteBlock
 * @author Nhóm 17
 */
public interface INoteBlockDAO {
    
    /**
     * Lấy tất cả các {@link NoteBlock} của một Note (lưu ý chỉ chứa các trường
     * thông tin của một NoteBlock, trừ editor, và không chứa bất cứ nội dung nào)
     * @param noteId id của note cần lấy các block
     * @return Một list chứa các thông tin cơ bản các block của note đó
     * @throws DAOException Xảy ra khi có lỗi về key hoặc câu lệnh, kết nối
     * @see com.noteapp.note.model.Note
     */
    List<NoteBlock> getAll(int noteId) throws DAOException;
    
    /**
     * Tạo một bản ghi lưu giữ các trường thông tin cơ bản của một {@link NoteBlock}
     * @param noteId id mà block này thuộc về
     * @param newNoteBlock Block mới
     * @return Block vừa được tạo
     * @throws DAOException Xảy ra khi có lỗi về key hoặc câu lệnh, kết nối
     * @see com.noteapp.note.model.Note
     * @see INoteBlockDAO#getAll(int) 
     */
    NoteBlock create(int noteId, NoteBlock newNoteBlock) throws DAOException;
    
    /**
     * Chỉnh sửa các trường thông tin cơ bản của một NoteBlock
     * @param noteId id mà block thuộc về
     * @param noteBlock NoteBlock cần chỉnh sửa
     * @throws DAOException Xảy ra khi có lỗi về key hoặc câu lệnh, kết nối
     * @see com.noteapp.note.model.Note
     * @see INoteBlockDAO#getAll(int) 
     */
    void update(int noteId, NoteBlock noteBlock) throws DAOException;
    
    /**
     * Xóa một NoteBlock trong CSDL, chú ý rằng lúc này, do mối quan hệ khóa ngoài
     * nên các bản ghi liên quan ở các CSDL kế thừa từ nó như TextBlock và SurveyBlock
     * cũng sẽ bị xóa ngay
     * @param blockId id của block bị xóa
     * @throws DAOException Xảy ra khi có lỗi về key hoặc câu lệnh, kết nối
     * @see com.noteapp.note.model.Note
     * @see INoteBlockDAO#getAll(int)
     * @see ITextBlockDAO
     * @see ISurveyBlockDAO
     */
    void delete(int blockId) throws DAOException;
}