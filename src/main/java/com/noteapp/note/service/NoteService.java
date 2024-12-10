package com.noteapp.note.service;

import com.noteapp.common.dao.DAOException;
import com.noteapp.common.dao.NotExistDataException;
import com.noteapp.note.dao.INoteBlockDAO;
import com.noteapp.note.dao.INoteDAO;
import com.noteapp.note.dao.INoteFilterDAO;
import com.noteapp.note.dao.ISurveyBlockDAO;
import com.noteapp.note.dao.ITextBlockDAO;
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
 * @see ITextBlockDAO
 * @see ISurveyBlockDAO
 */
public class NoteService {
    protected INoteDAO noteDAO;
    protected INoteFilterDAO noteFilterDAO;
    protected INoteBlockDAO noteBlockDAO;
    protected ITextBlockDAO textBlockDAO;
    protected ISurveyBlockDAO surveyBlockDAO;

    public NoteService() {
        
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
    
    /**
     * Tạo một {@link Note} mới và lưu vào trong CSDL
     * @param newNote Note mới cần tạo
     * @return Note vừa được tạo thành công
     * @throws NoteServiceException Xảy ra khi Note đã tồn tại, hoặc các
     * thao tác với CSDL bị lỗi
     * @see #open(int) 
     * @see INoteDAO#get(int) 
     * @see INoteDAO#create(Note)
     * @see INoteFilterDAO#create(int, NoteFilter) 
     * @see #createBlock(int, NoteBlock) 
     * @see DAOException
     */
    public Note create(Note newNote) throws NoteServiceException {
        getInstanceOfDAO();
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
                createBlock(newNote.getId(), newNoteBlock);
            }
            //Mở Note và trả về
            return this.open(newNote.getId());
        } catch (DAOException exByCreate) {
            throw new NoteServiceException(exByCreate.getMessage(), exByCreate.getCause());
        }
    }
    
    /**
     * Xóa một Note đã tồn tại trong CSDL
     * @param noteId id của Note cần xóa
     * @return Note vừa được xóa thành công
     * @throws NoteServiceException Xảy ra khi các thao tác tương ứng với CSDL 
     * bị lỗi
     * @see #open(int) 
     * @see INoteDAO#delete(int) 
     * @see DAOException
     */
    public Note delete(int noteId) throws NoteServiceException {
        getInstanceOfDAO();
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
    
    /**
     * Lấy tất cả các Note thuộc quyền sở hữu của User
     * @param author username của User sở hữu các Note này
     * @return Một List các Note thuộc quyền của User này nếu thành công
     * @throws NoteServiceException Xảy ra khi các thao tác với CSDL liên quan
     * bị lỗi
     * @see INoteDAO#getAll(String)
     * @see #open(int) 
     * @see DAOException
     */
    public List<Note> getAll(String author) throws NoteServiceException {
        getInstanceOfDAO();
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
    
    /**
     * Mở một Note, cụ thể là lấy tất cả các dữ liệu liên quan tới một Note từ 
     * các CSDL Note, NoteFilter, NoteBlock, TextBlock, SurveyBlock và trả về
     * @param noteId id của Note cần mở
     * @return Note được mở thành công
     * @throws NoteServiceException Xảy ra khi các thao tác với các CSDL liên quan bị lỗi
     * @see INoteDAO#get(int) 
     * @see INoteFilterDAO#getAll(int) 
     * @see #getAllBlocks(int, String)
     * @see DAOException
     */
    public Note open(int noteId) throws NoteServiceException {
        getInstanceOfDAO();
        try {
            //Lấy các thông tin cơ bản
            Note note = noteDAO.get(noteId);
            //Lấy các filter
            note.setFilters(noteFilterDAO.getAll(noteId));
            //Lấy các blocks
            List<NoteBlock> noteBlocks = this.getAllBlocks(noteId, note.getAuthor());
            note.setBlocks(noteBlocks);
            return note;
        } catch (DAOException exByGet) {
            exByGet.printStackTrace();
            throw new NoteServiceException(exByGet.getMessage(), exByGet.getCause());
        }
    } 
    
    /**
     * Lưu một Note, bản chất là cập nhật các thông tin của một Note vào các 
     * CSDL tương ứng. Nếu Note chưa tồn tại thì tnos sẽ được tạo mới và đưa 
     * vào các CSDL tương ứng.
     * @param note Note cần lưu
     * @return Note sau khi được cập nhật hoặc tạo mới thành công
     * @throws NoteServiceException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     * @see #create(Note)
     * @see INoteDAO#get(int) 
     * @see INoteDAO#update(Note)
     * @see INoteFilterDAO#deleteAll(int) 
     * @see INoteFilterDAO#create(int, NoteFilter)
     * @see #saveBlocks(int, List) 
     */
    public Note save(Note note) throws NoteServiceException {
        getInstanceOfDAO();
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
            saveBlocks(note.getId(), note.getBlocks());
            return note;
        } catch (DAOException exByUpdate) {
            exByUpdate.printStackTrace();
            throw new NoteServiceException(exByUpdate.getMessage(), exByUpdate.getCause());
        }
    }
    
    /**
     * Lấy tất cả các block của một Note
     * @param noteId id của Note chứa các block cần lấy
     * @return Một List các block của note đó (bao gồm cả các phiên bản tương
     * ứng của block)
     * @throws DAOException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     * @see INoteBlockDAO#getAll(int) 
     * @see ITextBlockDAO#getAll(int) 
     * @see ISurveyBlockDAO#getAll(int) 
     * @see TextBlock
     * @see SurveyBlock
     */
    protected List<NoteBlock> getAllBlocks(int noteId) throws DAOException {
        //Lấy các thông tin cơ bản của block trước
        List<NoteBlock> noteBlocks = noteBlockDAO.getAll(noteId);
        List<NoteBlock> returnBlocks = new ArrayList<>();
        //Dựa vào kiểu của block mà quyết định gọi DAO nào để lấy các phiên bản tương ứng
        for(NoteBlock noteBlock: noteBlocks) {
            int blockId = noteBlock.getId();
            switch (noteBlock.getBlockType()) {
                case TEXT -> {
                    List<TextBlock> textBlocks = textBlockDAO.getAll(blockId);
                    for(TextBlock textBlock: textBlocks) {
                        textBlock.setNoteBlock(noteBlock);
                        returnBlocks.add(textBlock);
                    }
                }

                case SURVEY -> {
                    List<SurveyBlock> surveyBlocks = surveyBlockDAO.getAll(blockId);
                    for(SurveyBlock surveyBlock: surveyBlocks) {
                        surveyBlock.setNoteBlock(noteBlock);
                        returnBlocks.add(surveyBlock);
                    }
                }
            }
        }
        return returnBlocks;
    }
        
    /**
     * Lấy tất cả các block của một Note với một phiên bản chỉnh sửa của một User
     * @param noteId id của Note chứa các block cần lấy
     * @param editor username của chủ phiên bản chỉnh sửa này
     * @return Một List các NoteBlock của phiên bản Note tương ứng
     * @throws DAOException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     * @see INoteBlockDAO#getAll(int) 
     * @see ITextBlockDAO#getAll(int) 
     * @see ISurveyBlockDAO#getAll(int) 
     * @see #getAllBlocks(int) 
     * @see TextBlock
     * @see SurveyBlock
     */
    protected List<NoteBlock> getAllBlocks(int noteId, String editor) throws DAOException {
        List<NoteBlock> noteBlocks = noteBlockDAO.getAll(noteId);
        List<NoteBlock> returnBlocks = new ArrayList<>();
        for(NoteBlock noteBlock: noteBlocks) {
            int blockId = noteBlock.getId();
            switch (noteBlock.getBlockType()) {
                case TEXT -> {
                    List<TextBlock> textBlocks = textBlockDAO.getAll(blockId);
                    for(TextBlock textBlock: textBlocks) {
                        if(editor.equals(textBlock.getEditor())) {
                            textBlock.setNoteBlock(noteBlock);
                            returnBlocks.add(textBlock);
                        }
                    }
                }

                case SURVEY -> {
                    List<SurveyBlock> surveyBlocks = surveyBlockDAO.getAll(blockId);
                    for(SurveyBlock surveyBlock: surveyBlocks) {
                        if(editor.equals(surveyBlock.getEditor())) {
                            surveyBlock.setNoteBlock(noteBlock);
                            returnBlocks.add(surveyBlock);
                        }
                    }
                }
            }
        }
        return returnBlocks;
    }
    
    /**
     * Tạo một block mới và lưu vào các CSDL tương ứng (NoteBlock, TextBlock, SurveyBlock)
     * @param noteId id của Note chứa mà block chuẩn bị tạo thuộc về
     * @param newBlock NoteBlock cần được tạo
     * @throws DAOException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     * @see INoteBlockDAO#create(int, NoteBlock)
     * @see ITextBlockDAO#create(TextBlock) 
     * @see ISurveyBlockDAO#create(SurveyBlock) 
     * @see TextBlock
     * @see SurveyBlock
     */
    protected void createBlock(int noteId, NoteBlock newBlock) throws DAOException {
        newBlock = noteBlockDAO.create(noteId, newBlock);
        switch (newBlock.getBlockType()) {
            case TEXT -> {
                TextBlock newTextBlock = (TextBlock) newBlock;
                textBlockDAO.create(newTextBlock);
            }
            case SURVEY -> {
                SurveyBlock newSurveyBlock = (SurveyBlock) newBlock;
                surveyBlockDAO.create(newSurveyBlock);
            }
        }
    }
    
    /**
     * Cập nhật một NoteBlock trong một Note
     * @param noteId id của Note chứa NoteBlock cần cập nhật
     * @param needUpdateBlock NoteBlock cần cập nhật
     * @throws DAOException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     * @see INoteBlockDAO#update(int, NoteBlock)
     * @see ITextBlockDAO#update(TextBlock) 
     * @see ISurveyBlockDAO#update(SurveyBlock) 
     * @see TextBlock
     * @see SurveyBlock
     */
    protected void updateBlock(int noteId, NoteBlock needUpdateBlock) throws DAOException {
        noteBlockDAO.update(noteId, needUpdateBlock);
        switch (needUpdateBlock.getBlockType()) {
            case TEXT -> {
                TextBlock needUpdateTextBlock = (TextBlock) needUpdateBlock;
                textBlockDAO.update(needUpdateTextBlock);
            }
            case SURVEY -> {
                SurveyBlock needUpdateSurveyBlock = (SurveyBlock) needUpdateBlock;
                surveyBlockDAO.update(needUpdateSurveyBlock);
            }
        }
    }
    
    /**
     * Xóa một NoteBlock nào đó
     * @param blockId id của block cần xóa
     * @throws DAOException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     */
    protected void deleteBlock(int blockId) throws DAOException {
        noteBlockDAO.delete(blockId);
    }
    
    /**
     * Lưu một số các blocks của một Note vào CSDL tương ứng
     * @param noteId id của Note chứa các blocks cần lưu
     * @param noteBlocks các block cần lưu
     * @throws DAOException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     * @see #getAllBlocks(int, String)
     * @see #createBlock(int, NoteBlock) 
     * @see #updateBlock(int, NoteBlock)
     * @see #deleteBlock(int) 
     * @see TextBlock
     * @see SurveyBlock
     */
    protected void saveBlocks(int noteId, List<NoteBlock> noteBlocks) throws DAOException {
        //Lấy các block của phiên bản note này
        String editor = noteBlocks.get(0).getEditor();
        List<NoteBlock> blocksInDB = this.getAllBlocks(noteId, editor);
        //Phân loại các note vào loại cần tạo mới, cần cập nhật, cần xóa
        List<NoteBlock> newBlocks = new ArrayList<>();
        List<NoteBlock> needUpdateBlocks = new ArrayList<>();
        List<NoteBlock> deletedBlocks = new ArrayList<>();
        for(NoteBlock noteBlock: noteBlocks) {
            if(blocksInDB.contains(noteBlock)) {
                needUpdateBlocks.add(noteBlock);
            } else {
                newBlocks.add(noteBlock);
            }
        }
        for(NoteBlock noteBlock: blocksInDB) {
            if(!noteBlocks.contains(noteBlock)) {
                deletedBlocks.add(noteBlock);
            }
        }
        //Thực hiện dịch vụ tương ứng với từng loại
        for(NoteBlock newBlock: newBlocks) {
            this.createBlock(noteId, newBlock);
        }
        for(NoteBlock needUpdateBlock: needUpdateBlocks) {
            this.updateBlock(noteId, needUpdateBlock);
        }
        for(NoteBlock deletedBlock: deletedBlocks) {
            this.deleteBlock(deletedBlock.getId());
        }
    }
}