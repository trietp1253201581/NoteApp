package com.noteapp.note.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Một transfer cho dữ liệu của các note block dạng survey
 * @author Nhóm 17
 * @version 1.0
 */
public class SurveyBlock extends NoteBlock {
    private Map<String, Boolean> surveyMap;

    public SurveyBlock() {
        super(BlockType.SURVEY);
        this.surveyMap = new HashMap<>();
    }

    public SurveyBlock(Map<String, Boolean> surveyMap) {
        super(BlockType.SURVEY);
        this.surveyMap = surveyMap;
    }

    public SurveyBlock(String header, String editor, BlockType blockType) {
        super(header, editor, blockType);
    }

    public SurveyBlock(Map<String, Boolean> surveyMap, int id, String header, String editor, BlockType blockType, int order) {
        super(id, header, editor, blockType, order);
        this.surveyMap = surveyMap;
    }

    public Map<String, Boolean> getSurveyMap() {
        return surveyMap;
    }

    public void setSurveyMap(Map<String, Boolean> surveyMap) {
        this.surveyMap = surveyMap;
    }
 
    /**
     * Chuyển dữ liệu từ một {@link NoteBlock} vào một {@link SurveyBlock}
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
        return "SurveyBlock{" + 
                "id=" + super.getId() + 
                ", header=" + super.getHeader() + 
                ", editor=" + super.getEditor() + 
                ", blockType=" + super.getBlockType() + 
                ", order=" + super.getOrder() +
                ", surveyMap=" + surveyMap +
                '}';
    }

    public JSONObject getSurveyJSONObject() {
        return new JSONObject(surveyMap);
    }
    
    public void setSurveyMap(JSONObject surveyJSONObject) {
        Map<String, Boolean> nowSurveyMap = new HashMap<>();
        Iterator<String> keys = surveyJSONObject.keys();
        
        while (keys.hasNext()) {
            String key = keys.next();
            try {
                boolean value = surveyJSONObject.getBoolean(key);
                nowSurveyMap.put(key, value);
            } catch (JSONException ex) {
                System.err.println(ex.getMessage());
            }
        }
        this.surveyMap = nowSurveyMap;
    }
}