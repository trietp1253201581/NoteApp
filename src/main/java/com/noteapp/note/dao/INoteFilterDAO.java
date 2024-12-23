package com.noteapp.note.dao;

import com.noteapp.common.dao.DAOException;
import com.noteapp.note.model.NoteFilter;
import java.util.List;

/**
 * Cung cấp các phương thức thao tác với CSDL NoteFilter
 * @author Nhóm 17
 */
public interface INoteFilterDAO {
    
    /**
     * Lấy tất cả các {@link NoteFilter} của một Note
     * @param noteId id của note chứa các filter cần lấy
     * @return Một List các filter của Note này
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh
     * @see com.noteapp.note.model.Note
     */
    List<NoteFilter> getAll(int noteId) throws DAOException;
    
    /**
     * Tạo một {@link NoteFilter} mới cho một Note
     * @param noteId id của Note 
     * @param newNoteFilter filter mới cần tạo
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh, khóa ngoài
     * @see com.noteapp.note.model.Note
     */
    void create(int noteId, NoteFilter newNoteFilter) throws DAOException;
    
    /**
     * Xóa tất cả các filter của một Note
     * @param noteId id của note chứa các filter cần xóa
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh
     */
    void deleteAll(int noteId) throws DAOException;
}
