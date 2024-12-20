package com.noteapp.note.service;

import com.noteapp.common.dao.DAOException;
import com.noteapp.common.dao.NotExistDataException;
import com.noteapp.note.dao.IShareNoteDAO;
import com.noteapp.note.dao.ShareNoteDAO;
import com.noteapp.note.model.Note;
import com.noteapp.note.model.NoteBlock;
import static com.noteapp.note.model.NoteBlock.BlockType.SURVEY;
import static com.noteapp.note.model.NoteBlock.BlockType.TEXT;
import com.noteapp.note.model.ShareNote;
import com.noteapp.note.model.SurveyBlock;
import com.noteapp.note.model.TextBlock;
import com.noteapp.user.dao.IUserDAO;
import com.noteapp.user.dao.UserDAO;
import java.util.ArrayList;
import java.util.List;

/**
 * Cung cấp các dịch vụ liên quan tới Note được chia sẻ (ShareNote)
 * @author Nhóm 17
 * @see IUserDAO
 * @see IShareNoteDAO
 */
public class ShareNoteService extends NoteService {
    protected IShareNoteDAO shareNoteDAO;
    protected IUserDAO userDAO;
    
    @Override
    protected void getInstanceOfDAO() {
        super.getInstanceOfDAO();
        shareNoteDAO = ShareNoteDAO.getInstance();
        userDAO = UserDAO.getInstance();
    }
    
    /**
     * Chia sẻ một Note tới một người chỉnh sửa khác theo một trong hai loại
     * (chỉ đọc hoặc có thể chỉnh sửa)
     * @param note Note cần được chia sẻ
     * @param editor username của người nhận Note này
     * @param shareType Loại chia sẻ (Chỉ đọc hoặc có thể Edit)
     * @return Một ShareNote (một Note được chia sẻ giữa nhiều người)
     * @throws NoteServiceException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     * @see IUserDAO#get(String)
     * @see IShareNoteDAO#get(int, String)
     * @see IShareNoteDAO#update(ShareNote) 
     * @see #open(int, String)
     * @see IShareNoteDAO#create(ShareNote)
     * @see DAOException
     */
    public ShareNote share(Note note, String editor, ShareNote.ShareType shareType) throws NoteServiceException {
        getInstanceOfDAO();
        //Kiểm tra User đã tồn tại hay chưa
        try {
            userDAO.get(editor);
        } catch (DAOException ex) {
            ex.printStackTrace();
            throw new NoteServiceException(ex.getMessage(), ex.getCause());
        } 
        //Set các thông tin của note cho shareNote
        int noteId = note.getId();
        ShareNote shareNote = new ShareNote();
        shareNote.setNote(note);
        //Kiểm tra xem Note đã được chia sẻ hay chưa
        try {
            //Nếu có thì chỉ cần update và open nó
            shareNoteDAO.get(noteId, editor);
            shareNoteDAO.update(shareNote);
            return this.open(note.getId(), editor);
        } catch (NotExistDataException ex1) {
            //Nếu không thì cần tạo mới trong CSDL
        } catch (DAOException ex2) {
            ex2.printStackTrace();
            throw new NoteServiceException(ex2.getMessage(), ex2.getCause());
        }
        //Kiểm tra note của User đã được chia sẻ cho người khác hay chưa
        try {
            //Nếu có thì chỉ cần update và open nó
            shareNoteDAO.get(noteId, note.getAuthor());
        } catch (NotExistDataException ex1) {
            shareNote.setShareType(ShareNote.ShareType.CAN_EDIT);
            shareNote.setEditor(note.getAuthor());   
            try {
                shareNoteDAO.create(shareNote);  
            } catch (DAOException ex3) {
                throw new NoteServiceException(ex3.getMessage(), ex3.getCause());
            }    
        } catch (DAOException ex2) {
            ex2.printStackTrace();
            throw new NoteServiceException(ex2.getMessage(), ex2.getCause());
        }
        try {
            //Tạo ShareNote mới
            shareNote.setEditor(editor);
            shareNote.setShareType(shareType);
            //Sao lưu các Block của tác giả sang người chỉnh sửa khác
            List<NoteBlock> authorBlocks = note.getBlocks();
            for(NoteBlock noteBlock: authorBlocks) {
                NoteBlock thisEditorBlock = noteBlock;
                thisEditorBlock.setEditor(shareNote.getEditor());
                switch (thisEditorBlock.getBlockType()) {
                    case TEXT -> {
                        TextBlock newTextBlock = (TextBlock) thisEditorBlock;
                        textBlockDAO.create(newTextBlock);
                    }
                    case SURVEY -> {
                        SurveyBlock newSurveyBlock = (SurveyBlock) thisEditorBlock;
                        surveyBlockDAO.create(newSurveyBlock);
                    }

                }
            }
            shareNoteDAO.create(shareNote);
            note.setPubliced(true);
            super.save(note);
            return this.open(shareNote.getId(), shareNote.getEditor());
        } catch (DAOException ex) {
            ex.printStackTrace();
            throw new NoteServiceException(ex.getMessage(), ex.getCause());
        }
    }
    
    /**
     * Mở một Note được chia sẻ (ShareNote)
     * @param noteId id của Note cần mở
     * @param editor username là người chỉnh sửa phiên bản Note này
     * @return Một ShareNote được mở thành công
     * @throws NoteServiceException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     * @see #open(int) 
     * @see IShareNoteDAO#get(int, String)
     * @see #getAllBlocks(int) 
     * @see DAOException
     */
    public ShareNote open(int noteId, String editor) throws NoteServiceException {
        getInstanceOfDAO();
        //Trước hết mở Note
        Note note = super.open(noteId);
        //Mở phiên bản Note của editor
        try {
            ShareNote shareNote = shareNoteDAO.get(noteId, editor);
            shareNote.setNote(note);
            //Lấy và phân loại block nào của editor này và cái nào của editor khác
            List<NoteBlock> blocks = super.getAllBlocks(noteId);
            List<NoteBlock> thisEditorBlocks = new ArrayList<>();
            for(NoteBlock noteBlock: blocks) {
                if(editor.equals(noteBlock.getEditor())) {
                    thisEditorBlocks.add(noteBlock);
                } else {
                    shareNote.addOtherEditorBlock(noteBlock);
                }
            }
            shareNote.setBlocks(thisEditorBlocks);
            return shareNote;
        } catch (DAOException ex) {
            ex.printStackTrace();
            throw new NoteServiceException(ex.getMessage(), ex.getCause());
        }
    }
    
    /**
     * Lấy tất cả các ShareNote được chia sẻ tới người chỉnh sửa này
     * @param editor username của User
     * @return Một List các ShareNote là các Note được chia sẻ tới User này
     * @throws NoteServiceException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     * @see IShareNoteDAO#getAll(String)
     * @see #open(int, String) 
     */
    public List<ShareNote> getAllReceived(String editor) throws NoteServiceException {
        getInstanceOfDAO();
        try {
            List<ShareNote> shareNotes = shareNoteDAO.getAll(editor);
            List<ShareNote> receivedNotes = new ArrayList<>();
            for(ShareNote shareNote: shareNotes) {
                receivedNotes.add(this.open(shareNote.getId(), editor));
            }
            return receivedNotes;
        } catch (DAOException ex) {
            throw new NoteServiceException(ex.getMessage(), ex.getCause());
        }
    }
}