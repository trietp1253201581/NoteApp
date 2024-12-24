package com.noteapp.note.service;

import com.noteapp.service.NoteAppServiceException;
import com.noteapp.note.model.Note;
import com.noteapp.note.model.ShareNote;
import java.util.List;

/**
 *
 * @author admin
 */
public interface IShareNoteService {
    /**
     * Chia sẻ một Note tới một người chỉnh sửa khác theo một trong hai loại
     * (chỉ đọc hoặc có thể chỉnh sửa)
     * @param note Note cần được chia sẻ
     * @param editor username của người nhận Note này
     * @param shareType Loại chia sẻ (Chỉ đọc hoặc có thể Edit)
     * @return Một ShareNote (một Note được chia sẻ giữa nhiều người)
     * @throws NoteAppServiceException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     */
    ShareNote share(Note note, String editor, ShareNote.ShareType shareType) throws NoteAppServiceException;
    
    /**
     * Mở một Note được chia sẻ (ShareNote)
     * @param noteId id của Note cần mở
     * @param editor username là người chỉnh sửa phiên bản Note này
     * @return Một ShareNote được mở thành công
     * @throws NoteAppServiceException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     */
    ShareNote open(int noteId, String editor) throws NoteAppServiceException;
    
    /**
     * Lấy tất cả các ShareNote được chia sẻ tới người chỉnh sửa này
     * @param editor username của User
     * @return Một List các ShareNote là các Note được chia sẻ tới User này
     * @throws NoteAppServiceException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     */
    List<ShareNote> getAllReceived(String editor) throws NoteAppServiceException;
}
