package com.noteapp.note.dao;

import com.noteapp.common.dao.DAOException;
import com.noteapp.note.model.SurveyBlock;
import java.util.List;

/**
 *
 * @author admin
 */
public interface ISurveyBlockDAO {
    
    /**
     * Lấy tất cả các phiên bản của một {@link SurveyBlock} (với các editor khác
     * nhau) đối với cùng một NoteBlock
     * @param blockId id của NoteBlock
     * @return Một List chứa các phiên bản chỉnh sửa bởi các editor khác nhau
     * đối với NoteBlock này
     * @throws DAOException Xảy ra khi có lỗi về kết nối và câu lệnh
     * @see com.noteapp.note.model.NoteBlock
     */
    List<SurveyBlock> getAll(int blockId) throws DAOException;
    
    /**
     * Tạo một {@link SurveyBlock} mới và lưu vào CSDL. Để có thể tạo được thành
     * công thì SurveyBlock này phải là một NoteBlock đã tồn tại và editor cũng phải
     * là một User đã tồn tại trong CSDL
     * @param newSurveyBlock SurveyBlock cần tạo
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh và khóa ngoài
     * @see com.noteapp.note.model.NoteBlock
     */
    void create(SurveyBlock newSurveyBlock) throws DAOException;
    
    /**
     * Cập nhật một {@link SurveyBlock} trong CSDL
     * @param surveyBlock SurveyBlock cần chỉnh sửa
     * @throws DAOException Xảy ra khi có lỗi về kết nối, câu lệnh
     */
    void update(SurveyBlock surveyBlock) throws DAOException;
}