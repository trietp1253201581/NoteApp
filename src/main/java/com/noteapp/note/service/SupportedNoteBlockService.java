package com.noteapp.note.service;

import com.noteapp.common.dao.DAOException;
import com.noteapp.common.service.CausedBySystemException;
import com.noteapp.common.service.NoteAppServiceException;
import com.noteapp.note.dao.IConcreateBlockDAO;
import com.noteapp.note.dao.INoteBlockDAO;
import com.noteapp.note.model.NoteBlock;
import static com.noteapp.note.model.NoteBlock.BlockType.SURVEY;
import static com.noteapp.note.model.NoteBlock.BlockType.TEXT;
import com.noteapp.note.model.SurveyBlock;
import com.noteapp.note.model.TextBlock;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class SupportedNoteBlockService {
    protected INoteBlockDAO noteBlockDAO;
    protected IConcreateBlockDAO<TextBlock> textBlockDAO;
    protected IConcreateBlockDAO<SurveyBlock> surveyBlockDAO;

    protected SupportedNoteBlockService(INoteBlockDAO noteBlockDAO, IConcreateBlockDAO<TextBlock> textBlockDAO, IConcreateBlockDAO<SurveyBlock> surveyBlockDAO) {
        this.noteBlockDAO = noteBlockDAO;
        this.textBlockDAO = textBlockDAO;
        this.surveyBlockDAO = surveyBlockDAO;
    }

    protected void setNoteBlockDAO(INoteBlockDAO noteBlockDAO) {
        this.noteBlockDAO = noteBlockDAO;
    }

    protected void setTextBlockDAO(IConcreateBlockDAO<TextBlock> textBlockDAO) {
        this.textBlockDAO = textBlockDAO;
    }

    protected void setSurveyBlockDAO(IConcreateBlockDAO<SurveyBlock> surveyBlockDAO) {
        this.surveyBlockDAO = surveyBlockDAO;
    }
    
    private void checkNullDAO() throws NoteAppServiceException {
        if (noteBlockDAO == null || textBlockDAO == null || surveyBlockDAO == null) {
            throw new CausedBySystemException("DAO is null!");
        }
    }
    
    private List<TextBlock> getTextBlocks(NoteBlock noteBlock) throws DAOException {
        List<TextBlock> returnBlocks = new ArrayList<>();
        List<TextBlock> textBlocks = textBlockDAO.getAll(noteBlock.getId());
        for(TextBlock textBlock: textBlocks) {
            textBlock.setNoteBlock(noteBlock);
            returnBlocks.add(textBlock);
        }
        return returnBlocks;
    }
    
    private TextBlock getTextBlock(NoteBlock noteBlock, String editor) throws DAOException {
        List<TextBlock> textBlocks = textBlockDAO.getAll(noteBlock.getId());
        for(TextBlock textBlock: textBlocks) {       
            if(editor.equals(textBlock.getEditor())) {
                textBlock.setNoteBlock(noteBlock);
                return textBlock;
            }
        }
        throw new DAOException("Not found!");
    }
    
    private List<SurveyBlock> getSurveyBlocks(NoteBlock noteBlock) throws DAOException {
        List<SurveyBlock> returnBlocks = new ArrayList<>();
        List<SurveyBlock> surveyBlocks = surveyBlockDAO.getAll(noteBlock.getId());
        for(SurveyBlock surveyBlock: surveyBlocks) {
            surveyBlock.setNoteBlock(noteBlock);
            returnBlocks.add(surveyBlock);
        }
        return returnBlocks;
    }
    
    private SurveyBlock getSurveyBlock(NoteBlock noteBlock, String editor) throws DAOException {
        List<SurveyBlock> surveyBlocks = surveyBlockDAO.getAll(noteBlock.getId());
        for(SurveyBlock surveyBlock: surveyBlocks) {        
            if(editor.equals(surveyBlock.getEditor())) {
                surveyBlock.setNoteBlock(noteBlock);
                return surveyBlock;
            }
        }
        throw new DAOException("Not found!");
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
    protected List<NoteBlock> getAll(int noteId) throws DAOException, NoteAppServiceException {
        checkNullDAO();
        //Lấy các thông tin cơ bản của block trước
        List<NoteBlock> noteBlocks = noteBlockDAO.getAll(noteId);
        List<NoteBlock> returnBlocks = new ArrayList<>();
        //Dựa vào kiểu của block mà quyết định gọi DAO nào để lấy các phiên bản tương ứng
        for(NoteBlock noteBlock: noteBlocks) {
            switch (noteBlock.getBlockType()) {
                case TEXT -> {
                    returnBlocks.addAll(getTextBlocks(noteBlock));
                }
                case SURVEY -> {
                    returnBlocks.addAll(getSurveyBlocks(noteBlock));
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
     * @see #getAll(int) 
     * @see TextBlock
     * @see SurveyBlock
     */
    protected List<NoteBlock> getAll(int noteId, String editor) throws DAOException, NoteAppServiceException {
        checkNullDAO();
        List<NoteBlock> noteBlocks = noteBlockDAO.getAll(noteId);
        List<NoteBlock> returnBlocks = new ArrayList<>();
        for(NoteBlock noteBlock: noteBlocks) {
            switch (noteBlock.getBlockType()) {
                case TEXT -> {
                    returnBlocks.add(getTextBlock(noteBlock, editor));
                }

                case SURVEY -> {
                    returnBlocks.add(getSurveyBlock(noteBlock, editor));
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
    protected void create(int noteId, NoteBlock newBlock) throws DAOException, NoteAppServiceException {
        checkNullDAO();
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
    
    protected void createOtherVersion(NoteBlock noteBlock, String otherEditor) throws DAOException, NoteAppServiceException {
        checkNullDAO();
        noteBlock.setEditor(otherEditor);
        switch (noteBlock.getBlockType()) {
            case TEXT -> {
                TextBlock newTextBlock = (TextBlock) noteBlock;
                textBlockDAO.create(newTextBlock);
            }
            case SURVEY -> {
                SurveyBlock newSurveyBlock = (SurveyBlock) noteBlock;
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
    protected void update(int noteId, NoteBlock needUpdateBlock) throws DAOException, NoteAppServiceException {
        checkNullDAO();
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
    protected void delete(int blockId) throws DAOException, NoteAppServiceException {
        checkNullDAO();
        noteBlockDAO.delete(blockId);
    }
    
    /**
     * Lưu một số các blocks của một Note vào CSDL tương ứng
     * @param noteId id của Note chứa các blocks cần lưu
     * @param noteBlocks các block cần lưu
     * @throws DAOException Xảy ra khi các thao tác với CSDL tương ứng
     * bị lỗi
     * @see #getAll(int, String)
     * @see #create(int, NoteBlock) 
     * @see #update(int, NoteBlock)
     * @see #delete(int) 
     * @see TextBlock
     * @see SurveyBlock
     */
    protected void save(int noteId, List<NoteBlock> noteBlocks) throws DAOException, NoteAppServiceException {
        //Lấy các block của phiên bản note này
        String editor = noteBlocks.get(0).getEditor();
        List<NoteBlock> blocksInDB = this.getAll(noteId, editor);
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
            this.create(noteId, newBlock);
        }
        for(NoteBlock needUpdateBlock: needUpdateBlocks) {
            this.update(noteId, needUpdateBlock);
        }
        for(NoteBlock deletedBlock: deletedBlocks) {
            this.delete(deletedBlock.getId());
        }
    }
}