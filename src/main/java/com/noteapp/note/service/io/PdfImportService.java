package com.noteapp.note.service.io;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class PdfImportService {
    private final PdfReader pdfReader;

    /**
     * Khởi tạo một service đọc file .pdf bằng api của itextpdf
     * @param inputFileDir Đường dẫn tới file cần đọc
     * @throws IOException Khi file không tồn tại hoặc việc đọc file bị lỗi
     */
    public PdfImportService(String inputFileDir) throws IOException {
        this.pdfReader = new PdfReader(inputFileDir);
    }
    
    /**
     * Đọc dữ liệu dạng văn bản từ file .pdf ở một trang xác định
     * @param page số thứ tự của trang cần đọc
     * @return Dữ liệu dạng văn bản được đọc trong trang này
     * @throws IOException nếu việc đọc dữ liệu bị lỗi
     */
    public String readTextAtPage(int page) throws IOException {
        return PdfTextExtractor.getTextFromPage(pdfReader, page);
    }
    
    /**
     * Đọc dữ liệu dạng văn bản từ file .pdf
     * @return Dữ liệu dạng văn bản được đọc
     * @throws IOException nếu việc đọc dữ liệu bị lỗi
     */
    public String readText() throws IOException {
        int numOfPages = pdfReader.getNumberOfPages();
        String res = "";
        for (int i = 1; i <= numOfPages; i++) {
            res += readTextAtPage(i);
        }
        return res;
    }
    
    /**
     * Đọc dữ liệu dạng văn bản từ file .pdf
     * @return Trả về một List lưu giữ dữ liệu ở mỗi trang
     * @throws IOException nếu việc đọc dữ liệu bị lỗi
     */
    public List<String> readTextEachPage() throws IOException {
        int numOfPages = pdfReader.getNumberOfPages();
        List<String> res = new ArrayList<>();
        for (int i = 1; i <= numOfPages; i++) {
            res.add(readTextAtPage(i));
        }
        return res;
    }
}
