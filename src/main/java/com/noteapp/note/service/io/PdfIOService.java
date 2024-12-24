package com.noteapp.note.service.io;

import com.itextpdf.text.DocumentException;
import com.noteapp.note.model.Note;
import com.noteapp.note.model.NoteBlock;
import com.noteapp.note.model.ShareNote;
import com.noteapp.note.model.SurveyBlock;
import com.noteapp.note.model.TextBlock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author admin
 */
public class PdfIOService implements FileIOService {
    private PdfImportService importService;
    private PdfExportService exportService;
    
    @Override
    public Note importNote(String inputFileDir) throws IOException {
        //Khởi tạo import service
        importService = new PdfImportService(inputFileDir);
        //Tạo note và đọc header, đọc mỗi page để lấy dữ liệu
        Note note = new Note();
        note.setHeader(inputFileDir);
        List<String> blocks = importService.readTextEachPage();
        //Lấy mỗi block là dữ liệu ở một page và thêm vào Note
        for (int i = 0; i < blocks.size(); i++) {
            TextBlock newTextBlock = new TextBlock();
            newTextBlock.setOrder(i + 1);
            newTextBlock.setHeader("page " + String.valueOf(i + 1));
            newTextBlock.setContent(blocks.get(i));
            note.getBlocks().add(newTextBlock);
        }
        return note;
    }
    
    @Override
    public void outputNote(String outputFileDir, Note outputNote) throws IOException {
        //Khởi tạo export service
        try {
            exportService = new PdfExportService(outputFileDir, outputNote.getHeader());
            //Thực hiện duyệt các block và lưu trữ dạng dữ liệu tương ứng
            for (NoteBlock block: outputNote.getBlocks()) {
                if (block.getBlockType() == NoteBlock.BlockType.TEXT) {
                    exportService.writeText(block.getHeader(), ((TextBlock) block).getContent());
                } else {
                    List<String> columns = new ArrayList<>();
                    columns.add("choice");
                    columns.add("your vote");
                    List<List<String>> datas = getTableDataFromSurvey(((SurveyBlock) block));
                    exportService.writeTable(block.getHeader(), columns, datas);
                }
            }
            exportService.endWrite();
        } catch (DocumentException ex) {
            throw new IOException(ex.getCause());
        }
    }
    
    @Override
    public void outputNote(String outputFileDir, ShareNote outputNote) throws IOException {
        //Khởi tạo export service
        try {
            exportService = new PdfExportService(outputFileDir, outputNote.getHeader());
            //Thực hiện duyệt các block và lưu trữ dạng dữ liệu tương ứng
            for (NoteBlock block: outputNote.getBlocks()) {
                if (block.getBlockType() == NoteBlock.BlockType.TEXT) {
                    exportService.writeText(block.getHeader(), ((TextBlock) block).getContent());
                } else {
                    List<String> columns = new ArrayList<>();
                    columns.add("choice");
                    columns.add("your vote");
                    columns.add("total vote");
                    columns.add("other vote");
                    List<List<String>> datas = getTableDataFromSurvey((SurveyBlock) block, outputNote.getOtherEditorBlocks());
                    exportService.writeTable(block.getHeader(), columns, datas);
                }
            }
            exportService.endWrite();
        } catch (DocumentException ex) {
            throw new IOException(ex.getCause());
        }
    }
    
    protected List<List<String>> getTableDataFromSurvey(SurveyBlock surveyBlock) {
        List<List<String>> datas = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry: surveyBlock.getSurveyMap().entrySet()) {
            List<String> newRow = new ArrayList<>();
            newRow.add(entry.getKey());
            newRow.add(String.valueOf(entry.getValue()));
            datas.add(newRow);
        }
        return datas;
    }
    
    protected List<List<String>> getTableDataFromSurvey(SurveyBlock surveyBlock, Map<Integer, List<NoteBlock>> otherEditors) {
        List<List<String>> datas = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry: surveyBlock.getSurveyMap().entrySet()) {
            //Lấy choice và your vote
            List<String> newRow = new ArrayList<>();
            String choice = entry.getKey();
            boolean isVoted = entry.getValue();
            int numVoted = 0;
            newRow.add(choice);
            
            if (isVoted) {
                newRow.add("yes");
                numVoted += 1;
            } else {
                newRow.add("no");
            }
            //Lấy số lượng và vote của các editor khác
            List<SurveyBlock> otherSurveyBlocks = findOthersBlocks(surveyBlock, otherEditors);
            List<String> others = new ArrayList<>();
            for (SurveyBlock otherSurveyBlock: otherSurveyBlocks) {
                if (otherSurveyBlock.getSurveyMap().get(choice)) {
                    numVoted += 1;
                    others.add(otherSurveyBlock.getEditor());
                }
            }
            
            newRow.add(String.valueOf(numVoted));
            newRow.add(convertOthers(others));
            
            datas.add(newRow);
        }
        return datas;
    }
    
    protected String convertOthers(List<String> others) {
        String otherStr = "";
        for (String otherVoted: others) {
            otherStr += otherVoted + ", ";
        }
        return otherStr;
    }
   
    protected List<SurveyBlock> findOthersBlocks(NoteBlock noteBlock, Map<Integer, List<NoteBlock>> otherEditors) {
        List<SurveyBlock> res = new ArrayList<>();
        if (noteBlock.getBlockType() == NoteBlock.BlockType.TEXT) {
            return res;
        }
        for (NoteBlock block: otherEditors.get(noteBlock.getId())) {
            res.add((SurveyBlock) block);
        }
        return res;
    }
}
