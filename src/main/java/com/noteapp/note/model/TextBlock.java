package com.noteapp.note.model;

/**
 * Một transfer cho dữ liệu của các note block dạng text
 * @author Nhóm 17
 * @version 1.0
 */
public class TextBlock extends NoteBlock {
    private String content;

    public TextBlock() {
        super(BlockType.TEXT);
    }

    public TextBlock(String content) {
        super(BlockType.TEXT);
        this.content = content;
    }

    public TextBlock(String content, String header, String editor, BlockType blockType) {
        super(header, editor, blockType);
        this.content = content;
    }

    public TextBlock(String content, int id, String header, String editor, BlockType blockType, int order) {
        super(id, header, editor, blockType, order);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * Chuyển dữ liệu từ một {@link NoteBlock} vào một {@link TextBlock}
     * @param noteBlock NoteBlock cần chuyển dữ liệu
     */
    public void setNoteBlock(NoteBlock noteBlock) {
        super.setId(noteBlock.getId());
        super.setHeader(noteBlock.getHeader());
        super.setBlockType(noteBlock.getBlockType());
        super.setOrder(noteBlock.getOrder());
    }
    
    @Override
    public String toString() {
        return "TextBlock{" + 
                "id=" + super.getId() + 
                ", header=" + super.getHeader() + 
                ", editor=" + super.getEditor() + 
                ", blockType=" + super.getBlockType() + 
                ", order=" + super.getOrder() +
                ", content=" + content +
                '}';
    }
}
