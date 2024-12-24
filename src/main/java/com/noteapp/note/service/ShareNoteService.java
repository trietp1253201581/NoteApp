package com.noteapp.note.service;

import com.noteapp.dao.DAOException;
import com.noteapp.dao.NotExistDataException;
import com.noteapp.service.CausedBySystemException;
import com.noteapp.service.NoteAppServiceException;
import com.noteapp.note.dao.IConcreateBlockDAO;
import com.noteapp.note.dao.INoteBlockDAO;
import com.noteapp.note.dao.INoteDAO;
import com.noteapp.note.dao.INoteFilterDAO;
import com.noteapp.note.dao.IShareNoteDAO;
import com.noteapp.note.model.Note;
import com.noteapp.note.model.NoteBlock;
import com.noteapp.note.model.ShareNote;
import com.noteapp.note.model.SurveyBlock;
import com.noteapp.note.model.TextBlock;
import java.util.ArrayList;
import java.util.List;

/**
 * Cung cấp các dịch vụ liên quan tới Note được chia sẻ (ShareNote)
 * @author Nhóm 17
 * @see IUserDAO
 * @see IShareNoteDAO
 */
public class ShareNoteService implements IShareNoteService {
    protected IShareNoteDAO shareNoteDAO;
    protected SupportedNoteBlockService blockService;
    protected INoteService noteService;

    public ShareNoteService(IShareNoteDAO shareNoteDAO, INoteDAO noteDAO, INoteFilterDAO noteFilterDAO, INoteBlockDAO noteBlockDAO, IConcreateBlockDAO<TextBlock> textBlockDAO, IConcreateBlockDAO<SurveyBlock> surveyBlockDAO) {
        this.shareNoteDAO = shareNoteDAO;
        noteService = new NoteService(noteDAO, noteFilterDAO, noteBlockDAO, textBlockDAO, surveyBlockDAO);
        blockService = new SupportedNoteBlockService(noteBlockDAO, textBlockDAO, surveyBlockDAO);
    }

    public void setBlockService(SupportedNoteBlockService blockService) {
        this.blockService = blockService;
    }

    public void setNoteService(INoteService noteService) {
        this.noteService = noteService;
    }

    public void setShareNoteDAO(IShareNoteDAO shareNoteDAO) {
        this.shareNoteDAO = shareNoteDAO;
    }
    
    private void checkNullDAO() throws NoteAppServiceException {
        if (shareNoteDAO == null) {
            throw new CausedBySystemException("DAO is null!");
        }
    }
    
    @Override
    public ShareNote share(Note note, String editor, ShareNote.ShareType shareType) throws NoteAppServiceException {
        checkNullDAO();
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
            throw new CausedBySystemException(ex2.getMessage(), ex2.getCause());
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
                throw new CausedBySystemException(ex3.getMessage(), ex3.getCause());
            }    
        } catch (DAOException ex2) {
            throw new CausedBySystemException(ex2.getMessage(), ex2.getCause());
        }
        try {
            //Tạo ShareNote mới
            shareNote.setEditor(editor);
            shareNote.setShareType(shareType);
            //Sao lưu các Block của tác giả sang người chỉnh sửa khác
            List<NoteBlock> authorBlocks = note.getBlocks();
            for(NoteBlock noteBlock: authorBlocks) {
                NoteBlock thisEditorBlock = noteBlock;
                blockService.createOtherVersion(thisEditorBlock, shareNote.getEditor());
            }
            shareNoteDAO.create(shareNote);
            note.setPubliced(true);
            noteService.save(note);
            return this.open(shareNote.getId(), shareNote.getEditor());
        } catch (DAOException ex) {
            throw new CausedBySystemException(ex.getMessage(), ex.getCause());
        }
    }
    
    @Override
    public ShareNote open(int noteId, String editor) throws NoteAppServiceException {
        checkNullDAO();
        //Trước hết mở Note
        Note note = noteService.open(noteId);
        //Mở phiên bản Note của editor
        try {
            ShareNote shareNote = shareNoteDAO.get(noteId, editor);
            shareNote.setNote(note);
            //Lấy và phân loại block nào của editor này và cái nào của editor khác
            List<NoteBlock> blocks = blockService.getAll(noteId);
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
            throw new CausedBySystemException(ex.getMessage(), ex.getCause());
        }
    }
    
    @Override
    public List<ShareNote> getAllReceived(String editor) throws NoteAppServiceException {
        checkNullDAO();
        try {
            List<ShareNote> shareNotes = shareNoteDAO.getAll(editor);
            List<ShareNote> receivedNotes = new ArrayList<>();
            for(ShareNote shareNote: shareNotes) {
                receivedNotes.add(this.open(shareNote.getId(), editor));
            }
            return receivedNotes;
        } catch (DAOException ex) {
            throw new CausedBySystemException(ex.getMessage(), ex.getCause());
        }
    }
}