package com.noteapp.note.service.io;

import com.noteapp.note.model.Note;
import com.noteapp.note.model.ShareNote;
import java.io.IOException;

/**
 *
 * @author admin
 */
public interface FileIOService {
    
    /**
     * Nhập dữ liệu từ một file vào một Note
     * @param inputFileDir Đường dẫn tới file chứa dữ liệu vào
     * @return Một Note chứa dữ liệu được đoc
     * @throws IOException Việc đọc dữ liệu bị lỗi
     */
    Note importNote(String inputFileDir) throws IOException;
    
    /**
     * Xuất dữ liệu từ một Note ra một file
     * @param outputFileDir Đường dẫn tới file chứa dữ liệu ra
     * @param outputNote Một Note chứa dữ liệu cần xuất
     * @throws IOException Việc đọc dữ liệu bị lỗi  
     */
    void outputNote(String outputFileDir, Note outputNote) throws IOException;
    
    /**
     * Xuất dữ liệu từ một Note ra một file
     * @param outputFileDir Đường dẫn tới file chứa dữ liệu ra
     * @param outputNote Một Note được chia sẻ chứa dữ liệu cần xuất
     * @throws IOException Việc đọc dữ liệu bị lỗi  
     */
    void outputNote(String outputFileDir, ShareNote outputNote) throws IOException;
}
