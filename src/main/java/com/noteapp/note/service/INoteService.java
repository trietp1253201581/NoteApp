package com.noteapp.note.service;

import com.noteapp.note.model.Note;
import java.util.List;

/**
 *
 * @author admin
 */
public interface INoteService {
    /**
     * Tạo một {@link Note} mới và lưu vào trong CSDL
     * @param newNote Note mới cần tạo
     * @return Note vừa được tạo thành công
     * @throws NoteServiceException Xảy ra khi Note đã tồn tại, hoặc các
     * thao tác với CSDL bị lỗi
     */
    Note create(Note newNote) throws NoteServiceException;
    
    /**
     * Xóa một Note đã tồn tại trong CSDL
     * @param noteId id của Note cần xóa
     * @return Note vừa được xóa thành công
     * @throws NoteServiceException Xảy ra khi các thao tác tương ứng với CSDL 
     * bị lỗi
     */
    Note delete(int noteId) throws NoteServiceException;
    
    /**
     * Lấy tất cả các Note thuộc quyền sở hữu của User
     * @param author username của User sở hữu các Note này
     * @return Một List các Note thuộc quyền của User này nếu thành công
     * @throws NoteServiceException Xảy ra khi các thao tác với CSDL liên quan
     * bị lỗi
     */
    List<Note> getAll(String author) throws NoteServiceException;
    
    /**
     * Mở một Note, cụ thể là lấy tất cả các dữ liệu liên quan tới một Note từ 
     * các CSDL Note, NoteFilter, NoteBlock, TextBlock, SurveyBlock và trả về
     * @param noteId id của Note cần mở
     * @return Note được mở thành công
     * @throws NoteServiceException Xảy ra khi các thao tác với các CSDL liên quan bị lỗi
     */
    Note open(int noteId) throws NoteServiceException;
    
    /**
     * Lưu một Note, bản chất là cập nhật các thông tin của một Note vào các 
     * CSDL tương ứng. Nếu Note chưa tồn tại thì tnos sẽ được tạo mới và đưa 
     * vào các CSDL tương ứng.
     * @param note Note cần lưu
     * @return Note sau khi được cập nhật hoặc tạo mới thành công
     * @throws NoteServiceException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     */
    Note save(Note note) throws NoteServiceException;
}
