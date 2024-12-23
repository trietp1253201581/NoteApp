package com.noteapp.note.service;

import com.noteapp.common.dao.DAOException;
import com.noteapp.common.dao.NotExistDataException;
import com.noteapp.note.dao.IConcreateBlockDAO;
import com.noteapp.note.dao.INoteBlockDAO;
import com.noteapp.note.dao.INoteDAO;
import com.noteapp.note.dao.INoteFilterDAO;
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
 * @see ITextBlockDAO
 * @see ISurveyBlockDAO
 */
public class NoteService implements INoteService {
    protected INoteDAO noteDAO;
    protected INoteFilterDAO noteFilterDAO;
    protected SupportedNoteBlockService blockService;

    public NoteService(INoteDAO noteDAO, INoteFilterDAO noteFilterDAO, INoteBlockDAO noteBlockDAO, IConcreateBlockDAO<TextBlock> textBlockDAO, IConcreateBlockDAO<SurveyBlock> surveyBlockDAO) {
        this.noteDAO = noteDAO;
        this.noteFilterDAO = noteFilterDAO;
        blockService = new SupportedNoteBlockService(noteBlockDAO, textBlockDAO, surveyBlockDAO);
    }

    public void setBlockService(SupportedNoteBlockService blockService) {
        this.blockService = blockService;
    }

    public void setNoteDAO(INoteDAO noteDAO) {
        this.noteDAO = noteDAO;
    }

    public void setNoteFilterDAO(INoteFilterDAO noteFilterDAO) {
        this.noteFilterDAO = noteFilterDAO;
    }
    
    private void checkNullDAO() throws NoteServiceException {
        if (noteDAO == null || noteFilterDAO == null) {
            throw new NoteServiceException("DAO is null!");
        }
    }
    
    @Override
    public Note create(Note newNote) throws NoteServiceException {
        checkNullDAO();
        int noteId = newNote.getId();
        //Kiểm tra note đã tồn tại hay chưa
        try {          
            noteDAO.get(noteId);
            throw new NoteServiceException("Already exist note!");
        } catch (NotExistDataException nedExByGet) {
            //Nếu chưa tồn tại thì tiếp tục
        } catch (DAOException exByGet) {
            throw new NoteServiceException(exByGet.getMessage(), exByGet.getCause());
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
            throw new NoteServiceException(exByCreate.getMessage(), exByCreate.getCause());
        }
    }
    
    @Override
    public Note delete(int noteId) throws NoteServiceException {
        checkNullDAO();
        try {
            //Lấy Note bằng cách mở
            Note deletedNote = this.open(noteId);
            //Xóa Note và trả về
            noteDAO.delete(noteId);
            return deletedNote;
        } catch (DAOException exByGetAndDelete) {
            throw new NoteServiceException(exByGetAndDelete.getMessage(), exByGetAndDelete.getCause());
        }
    }
    
    @Override
    public List<Note> getAll(String author) throws NoteServiceException {
        checkNullDAO();
        try {
            List<Note> notes = noteDAO.getAll(author);
            List<Note> returnNotes = new ArrayList<>();
            for(Note note: notes) {
                returnNotes.add(this.open(note.getId()));
            }
            return returnNotes;
        } catch (DAOException exByGetAll) {
            throw new NoteServiceException(exByGetAll.getMessage(), exByGetAll.getCause());
        }
    }
    
    @Override
    public Note open(int noteId) throws NoteServiceException {
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
            throw new NoteServiceException(exByGet.getMessage(), exByGet.getCause());
        }
    } 
    
    @Override
    public Note save(Note note) throws NoteServiceException {
        checkNullDAO();
        int noteId = note.getId();
        //Kiểm tra note đã tồn tại chưa
        try {
            noteDAO.get(noteId);
        } catch (NotExistDataException nedExByGet) {
            //Nếu chưa tồn tại thì tạo note mới
            return this.create(note);
        } catch (DAOException exByGet) {
            throw new NoteServiceException(exByGet.getMessage(), exByGet.getCause());
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
            throw new NoteServiceException(exByUpdate.getMessage(), exByUpdate.getCause());
        }
    }
}