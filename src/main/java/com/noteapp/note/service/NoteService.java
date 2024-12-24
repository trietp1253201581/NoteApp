package com.noteapp.note.service;

import com.noteapp.dao.DAOException;
import com.noteapp.dao.NotExistDataException;
import com.noteapp.service.CausedBySystemException;
import com.noteapp.service.CausedByUserException;
import com.noteapp.service.NoteAppServiceException;
import com.noteapp.note.dao.IConcreateBlockDAO;
import com.noteapp.note.dao.INoteBlockDAO;
import com.noteapp.note.dao.INoteDAO;
import com.noteapp.note.dao.INoteFilterDAO;
import com.noteapp.note.dao.NoteBlockDAO;
import com.noteapp.note.dao.NoteDAO;
import com.noteapp.note.dao.NoteFilterDAO;
import com.noteapp.note.dao.SurveyBlockDAO;
import com.noteapp.note.dao.TextBlockDAO;
import com.noteapp.note.model.Note;
import com.noteapp.note.model.NoteBlock;
import com.noteapp.note.model.NoteFilter;
import com.noteapp.note.model.SurveyBlock;
import com.noteapp.note.model.TextBlock;
import java.util.ArrayList;
import java.util.List;

/**
 * Cung cấp các dịch vụ liên quan tới Note
 * @author admin
 * @see INoteDAO
 * @see INoteFilterDAO
 * @see INoteBlockDAO
 * @see IConcreateBlockDAO
 */
public class NoteService implements INoteService {
    protected INoteDAO noteDAO;
    protected INoteFilterDAO noteFilterDAO;
    protected INoteBlockDAO noteBlockDAO;
    protected IConcreateBlockDAO<TextBlock> textBlockDAO;
    protected IConcreateBlockDAO<SurveyBlock> surveyBlockDAO;
    protected SupportedNoteBlockService blockService;

    public NoteService(INoteDAO noteDAO, INoteFilterDAO noteFilterDAO, INoteBlockDAO noteBlockDAO, IConcreateBlockDAO<TextBlock> textBlockDAO, IConcreateBlockDAO<SurveyBlock> surveyBlockDAO) {
        this.noteDAO = noteDAO;
        this.noteFilterDAO = noteFilterDAO;
        this.noteBlockDAO = noteBlockDAO;
        this.textBlockDAO = textBlockDAO;
        this.surveyBlockDAO = surveyBlockDAO;
        blockService = new SupportedNoteBlockService(noteBlockDAO, textBlockDAO, surveyBlockDAO);
    }


    /**
     * Lấy các thể hiện tương ứng cho các DAO
     */
    protected void getInstanceOfDAO() {
        noteDAO = NoteDAO.getInstance();
        noteFilterDAO = NoteFilterDAO.getInstance();
        noteBlockDAO = NoteBlockDAO.getInstance();
        textBlockDAO = TextBlockDAO.getInstance();
        surveyBlockDAO = SurveyBlockDAO.getInstance();
    }

    private void checkNullDAO() throws NoteAppServiceException {
        if (noteDAO == null || noteFilterDAO == null) {
            throw new CausedBySystemException("DAO is null!");
        }
    }
    
    @Override
    public Note create(Note newNote) throws NoteAppServiceException {
        checkNullDAO();
        int noteId = newNote.getId();
        //Kiểm tra note đã tồn tại hay chưa
        try {          
            noteDAO.get(noteId);
            throw new CausedByUserException("Already exist note!");
        } catch (NotExistDataException nedExByGet) {
            //Nếu chưa tồn tại thì tiếp tục
        } catch (DAOException exByGet) {
            throw new CausedBySystemException(exByGet.getMessage(), exByGet.getCause());
        }
        try {
            //Thêm các trường thông tin cơ bản vào CSDL Note
            newNote = noteDAO.create(newNote);
            //Thêm các filter của Note vào CSDL NoteFilter
            for(NoteFilter newNoteFilter: newNote.getFilters()) {
                noteFilterDAO.create(noteId, newNoteFilter);
            }
            //Thêm các block vào CSDL NoteBlock, TextBlock, SurveyBlock
            for(NoteBlock newNoteBlock: newNote.getBlocks()) {
                newNoteBlock.setEditor(newNote.getAuthor());
                blockService.create(newNote.getId(), newNoteBlock);
            }
            //Mở Note và trả về
            return this.open(newNote.getId());
        } catch (DAOException exByCreate) {
            throw new CausedBySystemException(exByCreate.getMessage(), exByCreate.getCause());
        }
    }
    
    @Override
    public Note delete(int noteId) throws NoteAppServiceException {
        checkNullDAO();
        try {
            //Lấy Note bằng cách mở
            Note deletedNote = this.open(noteId);
            //Xóa Note và trả về
            noteDAO.delete(noteId);
            return deletedNote;
        } catch (DAOException exByGetAndDelete) {
            throw new CausedBySystemException(exByGetAndDelete.getMessage(), exByGetAndDelete.getCause());
        }
    }

    @Override
    public List<Note> getAll(String author) throws NoteAppServiceException {
        checkNullDAO();
        try {
            List<Note> notes = noteDAO.getAll(author);
            List<Note> returnNotes = new ArrayList<>();
            for(Note note: notes) {
                returnNotes.add(this.open(note.getId()));
            }
            return returnNotes;
        } catch (DAOException exByGetAll) {
            throw new CausedBySystemException(exByGetAll.getMessage(), exByGetAll.getCause());
        }
    }
    
    @Override
    public Note open(int noteId) throws NoteAppServiceException {
        checkNullDAO();
        try {
            //Lấy các thông tin cơ bản
            Note note = noteDAO.get(noteId);
            //Lấy các filter
            note.setFilters(noteFilterDAO.getAll(noteId));
            //Lấy các blocks
            List<NoteBlock> noteBlocks = blockService.getAll(noteId, note.getAuthor());
            note.setBlocks(noteBlocks);
            return note;
        } catch (DAOException exByGet) {
            throw new CausedBySystemException(exByGet.getCause());
        }
    } 

    
    @Override
    public Note save(Note note) throws NoteAppServiceException {
        checkNullDAO();
        int noteId = note.getId();
        //Kiểm tra note đã tồn tại chưa
        try {
            noteDAO.get(noteId);
        } catch (NotExistDataException nedExByGet) {
            //Nếu chưa tồn tại thì tạo note mới
            return this.create(note);
        } catch (DAOException exByGet) {
            throw new CausedBySystemException(exByGet.getMessage(), exByGet.getCause());
        }
        try {
            //Cập nhật các thông tin cơ bản vào CSDL Note
            noteDAO.update(note);
            //Xóa tất cả các filter cũ của note
            noteFilterDAO.deleteAll(noteId);
            //Thêm lại các filter mới vào CSDL
            for(NoteFilter noteFilter: note.getFilters()) {
                noteFilterDAO.create(noteId, noteFilter);
            }
            //Gọi lưu các block vào CSDL
            blockService.save(note.getId(), note.getBlocks());
            return note;
        } catch (DAOException exByUpdate) {
            throw new CausedBySystemException(exByUpdate.getMessage(), exByUpdate.getCause());
        }
    }

}