package com.noteapp.note.dao;

import com.noteapp.common.dao.FailedExecuteException;
import com.noteapp.common.dao.DAOException;
import com.noteapp.common.dbconnection.MySQLDatabaseConnection;
import com.noteapp.common.dbconnection.SQLDatabaseConnection;
import com.noteapp.note.model.NoteBlock;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Triển khai các phương thức thao tác CSDL NoteBlock
 * @author Nhóm 17
 */
public class NoteBlockDAO implements INoteBlockDAO {
    protected SQLDatabaseConnection databaseConnection;
    protected Map<String, String> enableQueries;
    
    protected static final String SQL_FILE_DIR = "src/main/java/com/noteapp/note/db/NoteBlockQueries.sql";

    protected static final String DATABASE_HOST = "localhost";  
    protected static final int DATABASE_PORT = 3306;
    protected static final String DATABASE_NAME = "notelitedb";
    protected static final String DATABASE_USERNAME = "root";
    protected static final String DATABASE_PASSWORD = "Asensio1234@";

    protected static enum ColumnName {
        block_id, note_id, header, block_type, block_order;
    }

    protected static enum QueriesType {
        GET_ALL_BY_NOTE, CREATE, UPDATE, DELETE;
    }

    /**
     * Khởi tạo và lấy connection tới Database
     */
    private NoteBlockDAO() {
        //init connection
        databaseConnection = new MySQLDatabaseConnection
            (DATABASE_HOST, DATABASE_PORT, DATABASE_NAME, 
                    DATABASE_USERNAME, DATABASE_PASSWORD);
        databaseConnection.connect();
        //Get enable Queries
        databaseConnection.readSQL(SQL_FILE_DIR);
        enableQueries = databaseConnection.getEnableQueries();
    }

    private static class SingletonHelper {
        private static final NoteBlockDAO INSTANCE = new NoteBlockDAO();
    }    
    
    /**
     * Lấy thể hiện duy nhất của lớp này
     * @return Instance duy nhất
     */
    public static NoteBlockDAO getInstance() {
        return SingletonHelper.INSTANCE;
    }
    
    /**
     * Lấy và trả về một câu lệnh được chuẩn bị sẵn theo loại truy vấn
     * @param queriesType loại truy vấn
     * @return Một {@link PrepareStatement} là môt câu lệnh truy vấn SQL
     * đã được chuẩn bị sẵn tương ứng với loại truy vấn được yêu cầu
     * @throws SQLException Nếu kết nối tới CSDL không tồn tại hoặc
     * xảy ra vấn đề khi tạo câu lệnh
     * @see java.sql.Connection#prepareStatement(String)
     */
    protected PreparedStatement getPrepareStatement(QueriesType queriesType) throws SQLException {
        //Kiểm tra kết nối
        if (databaseConnection.getConnection() == null) {
            throw new SQLException("Connection null!");
        }
        
        //Lấy query và truyền vào connection
        String query = enableQueries.get(queriesType.toString());
        return databaseConnection.getConnection().prepareStatement(query);
    }
    
    /**
     * Lấy và trả về một câu lệnh được chuẩn bị sẵn theo loại truy vấn,
     * đồng thời câu lệnh này sẽ dẫn tới việc tự tạo một key trong CSDL
     * @param queriesType loại truy vấn
     * @param autoGeneratedKeys một số nguyên miêu tả generated keys
     * @return Một câu lệnh được chuẩn bị sẵn
     * @throws SQLException Nếu kết nối tới CSDL không tồn tại hoặc
     * xảy ra vấn đề khi tạo câu lệnh
     * @see java.sql.Connection#prepareStatement(String, int)
     */
    protected PreparedStatement getPrepareStatement(QueriesType queriesType, int autoGeneratedKeys) throws SQLException {
        //Kiểm tra kết nối
        if (databaseConnection.getConnection() == null) {
            throw new SQLException("Connection null!");
        }
        
        //Lấy query và truyền vào connection
        String query = enableQueries.get(queriesType.toString());
        return databaseConnection.getConnection().prepareStatement(query, autoGeneratedKeys);
    }

    @Override 
    public List<NoteBlock> getAll(int noteId) throws DAOException {
        try {
            //Thực thi truy vấn SQL và lấy kết quả là một bộ dữ liệu
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.GET_ALL_BY_NOTE);
            preparedStatement.setInt(1, noteId);
            ResultSet resultSet = preparedStatement.executeQuery();
            //Chuyển từng hàng dữ liệu sang noteBlock và thêm vào list
            List<NoteBlock> noteBlocks = new ArrayList<>();
            while (resultSet.next()) {
                NoteBlock noteBlock = new NoteBlock();
                //Set dữ liệu cho noteBlock
                noteBlock.setId(resultSet.getInt(ColumnName.block_id.toString()));
                noteBlock.setHeader(resultSet.getString(ColumnName.header.toString()));
                noteBlock.setBlockType(NoteBlock.BlockType.valueOf(resultSet.getString(ColumnName.block_type.toString())));
                noteBlock.setOrder(resultSet.getInt(ColumnName.block_order.toString()));
                noteBlocks.add(noteBlock);
            }    
            //Nếu noteBlocks rỗng thì ném ngoại lệ là danh sách trống
            return noteBlocks;
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
    
    @Override
    public NoteBlock create(int noteId, NoteBlock newNoteBlock) throws DAOException {
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.CREATE, 
                    PreparedStatement.RETURN_GENERATED_KEYS);
            //Set các tham số cho truy vấn
            preparedStatement.setInt(1, noteId);
            preparedStatement.setString(2, newNoteBlock.getHeader());
            preparedStatement.setString(3, newNoteBlock.getBlockType().toString());
            preparedStatement.setInt(4, newNoteBlock.getOrder());
            
            if(preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while(resultSet.next()) {
                newNoteBlock.setId(resultSet.getInt(1));
            }
            return newNoteBlock;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new FailedExecuteException(ex.getCause());
        }
    }

    @Override
    public void update(int noteId, NoteBlock noteBlock) throws DAOException {
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.UPDATE);
            //Set các tham số cho truy vấn 
            preparedStatement.setInt(1, noteId);
            preparedStatement.setString(2, noteBlock.getHeader());
            preparedStatement.setString(3, noteBlock.getBlockType().toString());
            preparedStatement.setInt(4, noteBlock.getOrder());
            preparedStatement.setInt(5, noteBlock.getId());
            if(preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
    
    @Override
    public void delete(int blockId) throws DAOException {
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.DELETE);
            //Set các tham số cho truy vấn
            preparedStatement.setInt(1, blockId);

            if(preparedStatement.executeUpdate() < 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
}