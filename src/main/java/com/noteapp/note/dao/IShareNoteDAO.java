package com.noteapp.note.dao;

import com.noteapp.dao.DAOException;
import com.noteapp.note.model.ShareNote;
import java.util.List;
/**
 * Cung cấp các phương thức thao tác với CSDL ShareNote
 * @author Nhóm 17
 */
public interface IShareNoteDAO {
    
    /**
     * Lấy tất cả các {@link ShareNote} được chia sẻ tới một User
     * @param editor username của User này
     * @return Một list các ShareNote được chia sẻ tới User này
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh
     */
    List<ShareNote> getAll(String editor) throws DAOException;
    
    /**
     * Lấy một phiên bản của một Note được chỉnh sửa bởi một User
     * @param noteId id của Note
     * @param editor username của User chỉnh sửa
     * @return Phiên bản {@link ShareNote} được chỉnh sửa bởi User này
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh
     */
    ShareNote get(int noteId, String editor) throws DAOException;
    
    /**
     * Tạo một bản ghi ShareNote mới trong CSDL (gồm các thông tin về
     * note, người được chia sẻ)
     * @param newShareNote ShareNote mới
     * @return ShareNote được tạo
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh
     */
    ShareNote create(ShareNote newShareNote) throws DAOException;
    
    /**
     * Chỉnh sửa một số trường thông tin của ShareNote trong CSDL
     * @param shareNote ShareNote cần chỉnh sửa
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh
     */
    void update(ShareNote shareNote) throws DAOException;
    
    /**
     * Xóa một phiên bản {@link ShareNote} của một Note với người chỉnh
     * sửa là một User
     * @param noteId id của Note
     * @param editor username của User chỉnh sửa Note này 
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh
     */
    void delete(int noteId, String editor) throws DAOException;
    
    /**
     * Xóa tất cả các phiên bản được chia sẻ của một Note, nói cách khác
     * đặt Note này về trạng thái PRIVATE
     * @param noteId id của Note cần khóa
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh
     */
    void deleteAll(int noteId) throws DAOException;
}