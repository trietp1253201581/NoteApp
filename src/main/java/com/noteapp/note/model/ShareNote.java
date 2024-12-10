package com.noteapp.note.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Một transfer cho dữ liệu của các note được chia sẻ
 * @author Nhóm 17
 * @version 1.0
 */
public class ShareNote extends Note {
    private String editor;
    private ShareType shareType;
    private Map<String, List<NoteBlock>> otherEditorBlocks;
    
    /**
     * Định nghĩa các kiểu Share
     */
    public static enum ShareType {
        READ_ONLY, CAN_EDIT;
    }
    
    public ShareNote() {
        super();
        this.editor = "";
        this.shareType = ShareType.READ_ONLY;
        this.otherEditorBlocks = new HashMap<>();
    }

    public ShareNote(String receiver, ShareType shareType, Map<String, List<NoteBlock>> otherEditorBlocks) {
        super();
        this.editor = receiver;
        this.shareType = shareType;
        this.otherEditorBlocks = otherEditorBlocks;
    }

    public ShareNote(String receiver, ShareType shareType, Map<String, List<NoteBlock>> otherEditorBlocks, int id, String author, String header, List<NoteBlock> blocks, Date lastModifiedDate, List<NoteFilter> filters) {
        super(id, author, header, blocks, lastModifiedDate, filters);
        this.editor = receiver;
        this.shareType = shareType;
        this.otherEditorBlocks = otherEditorBlocks;
    }

    public String getEditor() {
        return editor;
    }
    
    public ShareType getShareType() {
        return shareType;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }
    
    public void setShareType(ShareType shareType) {
        this.shareType = shareType;
    }

    public Map<String, List<NoteBlock>> getOtherEditorBlocks() {
        return otherEditorBlocks;
    }

    public void setOtherEditorBlocks(Map<String, List<NoteBlock>> otherEditorBlocks) {
        this.otherEditorBlocks = otherEditorBlocks;
    }
    
    public List<NoteBlock> getOtherEditorBlocksOf(String header) {
        return otherEditorBlocks.get(header);
    }
    
    public void setOtherEditorBlocksOf(String editor, List<NoteBlock> otherEditorBlocksOf) {
        otherEditorBlocks.put(editor, otherEditorBlocksOf);
    }
    
    public void addOtherEditorBlock(NoteBlock otherEditorBlock) {
        String blockHeader = otherEditorBlock.getHeader();
        if(!otherEditorBlocks.containsKey(blockHeader)) {
            otherEditorBlocks.put(blockHeader, new ArrayList<>());
        }
        otherEditorBlocks.get(blockHeader).add(otherEditorBlock);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.editor);
        return hash;
    }

    /**
     * So sánh một {@link Object} với {@link ShareNote} này.
     * @param obj Object cần so sánh
     * @return {@code true} nếu obj là một thể hiện của ShareNote,
     * thể hiện Note tương ứng của obj và ShareNote này bằng nhau và
     * thuộc tính {@code editor} của obj bằng với
     * {@code editor} của Note, {@code false} nếu ngược lại.
     * @see hashCode()
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null) {
            return false;
        }
        if(!(obj instanceof ShareNote)) {
            return false;
        }
        final ShareNote other = (ShareNote) obj;
        final Note otherNote = (Note) other;
        if(!super.equals(otherNote)) {
            return false;
        }
        return Objects.equals(this.editor, other.editor);
    }
    
    /**
     * Chuyển dữ liệu từ một {@link Note} vào một {@link ShareNote}
     * @param note Note cần chuyển dữ liệu
     */
    public void setNote(Note note) {
        super.setId(note.getId());
        super.setAuthor(note.getAuthor());
        super.setHeader(note.getHeader());
        super.setBlocks(note.getBlocks());
        super.setLastModifiedDate(note.getLastModifiedDate());
        super.setFilters(note.getFilters());
        super.setPubliced(note.isPubliced());
    }

    @Override
    public String toString() {
        return "ShareNote{" + 
                "editor=" + editor + 
                ", shareType=" + shareType + 
                ", otherEditorBlocks=" + otherEditorBlocks + 
                '}';
    }
}