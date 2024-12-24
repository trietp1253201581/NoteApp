package com.noteapp.note.dao;

import com.noteapp.dao.FailedExecuteException;
import com.noteapp.dao.DAOException;
import com.noteapp.dao.connection.MySQLDatabaseConnection;
import com.noteapp.dao.sql.SQLReader;
import com.noteapp.note.model.TextBlock;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai các phương thức thao tác CSDL TextBlock
 * @author Nhóm 17
 */
public class TextBlockDAO extends AbstractNoteDAO implements IConcreateBlockDAO<TextBlock> {
    
    protected static final String SQL_FILE_DIR = "src/main/java/com/noteapp/note/db/TextBlockQueries.sql";

    protected static enum ColumnName {
        block_id, editor, content;
    }
    
    protected static enum QueriesType {
        GET_ALL_BY_BLOCK, CREATE, UPDATE;
    }
    
    private TextBlockDAO() {
        //init connection
        setDatabaseConnection(new MySQLDatabaseConnection(DATABASE_HOST, DATABASE_PORT, 
            DATABASE_NAME, DATABASE_USERNAME, DATABASE_PASSWORD));
        initConnection();
        //get enable query
        setFileReader(new SQLReader());
        getEnableQueriesFrom(SQL_FILE_DIR);
    }
    
    private static class SingletonHelper {
        private static final TextBlockDAO INSTANCE = new TextBlockDAO();
    }
    
    /**
     * Trả về một thể hiện duy nhất của DAO này
     * @return Thể hiện duy nhất của TextBlockDAO
     */
    public static TextBlockDAO getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public List<TextBlock> getAll(int blockId) throws DAOException {
        try {
            //Thực thi truy vấn SQL và lấy kết quả là một bộ dữ liệu
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.GET_ALL_BY_BLOCK.toString());
            preparedStatement.setInt(1, blockId);
            ResultSet resultSet = preparedStatement.executeQuery();
            //Chuyển từng hàng dữ liệu sang textBlock và thêm vào list
            List<TextBlock> textBlocks = new ArrayList<>();
            while (resultSet.next()) {
                TextBlock textBlock = new TextBlock();
                //Set dữ liệu cho textBlock
                textBlock.setId(resultSet.getInt(ColumnName.block_id.toString()));
                textBlock.setEditor(resultSet.getString(ColumnName.editor.toString()));
                textBlock.setContent(resultSet.getString(ColumnName.content.toString()));
                textBlocks.add(textBlock);
            }    
            
            return textBlocks;
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }

    @Override
    public void create(TextBlock newTextBlock) throws DAOException {
        try {
            //Thực thi truy vấn SQL và lấy kết quả là một bộ dữ liệu
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.CREATE.toString());
            preparedStatement.setInt(1, newTextBlock.getId());
            preparedStatement.setString(2, newTextBlock.getEditor());
            preparedStatement.setString(3, newTextBlock.getContent());
            //Chuyển từng hàng dữ liệu sang textBlock và thêm vào list
            if(preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }

    @Override
    public void update(TextBlock textBlock) throws DAOException {
        try {
            //Thực thi truy vấn SQL và lấy kết quả là một bộ dữ liệu
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.UPDATE.toString());
            preparedStatement.setString(1, textBlock.getContent());
            preparedStatement.setInt(2, textBlock.getId());
            preparedStatement.setString(3, textBlock.getEditor());
            
            //Chuyển từng hàng dữ liệu sang textBlock và thêm vào list
            if(preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
}