package com.noteapp.note.service.io;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

/**
 *
 * @author admin
 */
public class PdfExportService {
    private final Document document;
    private Chapter allElements; 

    /**
     * Khởi tạo một Service giúp export ra định dạng file .pdf sử dụng api của itextpdf
     * @param outputFileDir Đường dẫn tới file cần ghi
     * @param header Đầu đề của file cần ghi
     * @throws FileNotFoundException Khi không tìm thấy đường dẫn để ghi file
     * @throws DocumentException Khi không thể ghi header vào file
     * @see PageSize
     */
    public PdfExportService(String outputFileDir, String header) throws FileNotFoundException, DocumentException {
        document = new Document(PageSize.A4, 50, 30, 30, 30);
        PdfWriter.getInstance(document, new FileOutputStream(outputFileDir));
        document.open();
        setHeaderOfFile(header);
    }
    
    /**
     * Khởi tạo một Service giúp export ra định dạng file .pdf sử dụng api của itextpdf
     * @param pageSize Size của mỗi page, có thể là A0->A10, etc.
     * @param marginLeft Độ rộng lề trái
     * @param marginRight Độ rộng lề phải
     * @param marginTop Độ rộng lề trên
     * @param marginBottom Độ rộng lề dưới
     * @param outputFileDir Đường dẫn tới file cần ghi
     * @param header Đầu đề của file cần ghi
     * @throws FileNotFoundException Khi không tìm thấy đường dẫn để ghi file
     * @throws DocumentException Khi không thể ghi header vào file
     * @see PageSize
     */
    public PdfExportService(float marginLeft, float marginRight, float marginTop, float marginBottom, String outputFileDir, String header) throws FileNotFoundException, DocumentException {
        document = new Document(PageSize.A4, marginLeft, marginRight, marginTop, marginBottom);
        PdfWriter.getInstance(document, new FileOutputStream(outputFileDir));
        document.open();
        setHeaderOfFile(header);
    }

    protected final void setHeaderOfFile(String fileHeader) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 25, Font.BOLD);
        Paragraph fileHeaderParagraph = new Paragraph(fileHeader, headerFont);
        allElements = new Chapter(fileHeaderParagraph, 0);
        allElements.setNumberDepth(0);
    }

    /**
     * Thêm một đoạn text dạng Paragraph và có header vào file document đã khởi tạo
     * @param header header của đoạn text
     * @param content nội dung của đoạn text
     */
    public void writeText(String header, String content) {
        //Thiết lập định dạng và nội dung cho header dưới dạng 1 section
        Font headerFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 15, Font.BOLDITALIC);
        Paragraph headerOfSection = new Paragraph(header, headerFont);
        Section thisBlock = allElements.addSection(headerOfSection);
        thisBlock.setNumberDepth(0);
        //Thêm nội dung vào section
        Paragraph newParagraph = new Paragraph(content);
        thisBlock.add(newParagraph);
    }

    /**
     * Thêm một bảng vào document
     * @param header tiêu đề của bảng
     * @param columns danh sách tiêu đề của các cột
     * @param datas một list 2 chiều lưu giữ data theo từng hàng
     */
    public void writeTable(String header, List<String> columns, List<List<String>> datas) {
        //Thiết lập định dạng và nội dung cho header dưới dạng 1 section
        Font headerFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 15, Font.BOLDITALIC);
        Paragraph headerOfSection = new Paragraph(header, headerFont);
        Section thisBlock = allElements.addSection(headerOfSection);
        thisBlock.setNumberDepth(0);
        //Thêm một bảng
        int numCols = columns.size();
        PdfPTable table = new PdfPTable(numCols);
        table.setSpacingBefore(25);
        table.setSpacingAfter(25);
        //Thêm các cột
        for (String column: columns) {
            Font columnHeaderFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 13, Font.BOLD);
            PdfPCell newCell = new PdfPCell(new Phrase(column, columnHeaderFont));
            table.addCell(newCell);
        }
        //Tìm số hàng cần thiết
        int numRows = datas.size();
        //Duyệt từng hàng để thêm dữ liệu, nếu có một cột bị hết dữ liệu thì sẽ thêm chuỗi rỗng
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                try {
                    table.addCell(datas.get(i).get(j));
                } catch (IndexOutOfBoundsException e) {
                    table.addCell("");
                }
            }
        }
        thisBlock.add(table);
    }
    
    /**
     * Kết thúc việc ghi dữ liệu ra file .pdf. Bạn phải gọi phương thức này
     * để việc ghi dữ liệu được hoàn tất, nếu không, có thể dẫn tới trường
     * hợp không được ghi thành công.
     * @return {@code true} nếu quá trình ghi dữ liệu được hoàn thành,
     * {@code false} nếu ngược lại
     */
    public boolean endWrite() {
        try {
            document.add(allElements);
            document.close();
            return true;
        } catch (DocumentException e) {
            return false;
        }
    }
}
