package com.noteapp.note.dao;

import com.noteapp.common.dao.DAOKeyException;
import com.noteapp.common.dao.NotExistDataException;
import com.noteapp.common.dao.FailedExecuteException;
import com.noteapp.common.dao.DAOException;
import com.noteapp.common.dbconnection.MySQLDatabaseConnection;
import com.noteapp.common.dbconnection.SQLDatabaseConnection;
import com.noteapp.note.model.ShareNote;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Triển khai các phương thức thao tác dữ liệu với ShareNote
 * @author Nhóm 17
 */
public class ShareNoteDAO implements IShareNoteDAO {
    protected SQLDatabaseConnection databaseConnection;
    protected Map<String, String> enableQueries;
    
    protected static final String SQL_FILE_DIR = "src/main/java/com/noteapp/note/db/ShareNoteQueries.sql";

    protected static final String DATABASE_HOST = "localhost";  
    protected static final int DATABASE_PORT = 3306;
    protected static final String DATABASE_NAME = "notelitedb";
    protected static final String DATABASE_USERNAME = "root";
    protected static final String DATABASE_PASSWORD = "Asensio1234@";

    protected static enum ColumnName {
        note_id, editor, share_type; 
    }
    
    protected static enum QueriesType {
        GET_ALL_BY_EDITOR, GET, CREATE, UPDATE, DELETE, DELETE_ALL;
    }

    private ShareNoteDAO() {
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
        public static final ShareNoteDAO INSTANCE = new ShareNoteDAO();
    }
    
    /**
     * Lấy thể hiện duy nhất của lớp này
     * @return Instance duy nhất
     */
    public static ShareNoteDAO getInstance() {
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

    @Override
    public List<ShareNote> getAll(String editor) throws DAOException {
        try {
            //Set các tham số, thực thi truy vấn và lấy kết quả
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.GET_ALL_BY_EDITOR);
            preparedStatement.setString(1, editor);
            ResultSet resultSet = preparedStatement.executeQuery();
            //Duyệt các hàng kết quả
            List<ShareNote> shareNotes = new ArrayList<>();
            while (resultSet.next()) {
                ShareNote shareNote = new ShareNote();
                shareNote.setId(resultSet.getInt(ColumnName.note_id.toString()));
                shareNote.setEditor(resultSet.getString(ColumnName.editor.toString()));
                shareNote.setShareType(ShareNote.ShareType.valueOf(resultSet.getString(ColumnName.share_type.toString())));
                //Thêm note vào list
                shareNotes.add(shareNote);
            }
            
            return shareNotes;
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }       
    }

    @Override
    public ShareNote get(int noteId, String editor) throws DAOException {
        //Kiểm tra key
        if ("".equals(editor)) {
            throw new DAOKeyException();
        }

        try {
            //Set tham số và thực thi truy vấn
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.GET);
            preparedStatement.setInt(1, noteId);
            preparedStatement.setString(2, editor);

            ResultSet resultSet = preparedStatement.executeQuery();
            ShareNote shareNote = new ShareNote();
            while (resultSet.next()) {                
                shareNote.setId(resultSet.getInt(ColumnName.note_id.toString()));
                shareNote.setEditor(resultSet.getString(ColumnName.editor.toString()));
                shareNote.setShareType(ShareNote.ShareType.valueOf(resultSet.getString(ColumnName.share_type.toString())));
            }
            //Nếu là giá trị mặc định thí ném ra ngoại lệ
            if(shareNote.isDefaultValue()) {
                throw new NotExistDataException();
            }
            return shareNote;
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }

    @Override
    public ShareNote create(ShareNote newShareNote) throws DAOException {
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.CREATE);
            preparedStatement.setInt(1, newShareNote.getId());
            preparedStatement.setString(2, newShareNote.getEditor());
            preparedStatement.setString(3, newShareNote.getShareType().toString());
            if(preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
            return newShareNote;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new FailedExecuteException(ex.getCause());
        }
    }

    @Override
    public void update(ShareNote shareNote) throws DAOException {
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.UPDATE);
            preparedStatement.setString(1, shareNote.getShareType().toString());
            preparedStatement.setInt(2, shareNote.getId());
            preparedStatement.setString(3, shareNote.getEditor());

            if(preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }

    @Override
    public void delete(int noteId, String editor) throws DAOException {
        //Kiểm tra key
        if ("".equals(editor)) {
            throw new DAOKeyException();
        }

        try {
            //Set tham số và thực thi truy vấn
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.DELETE);
            preparedStatement.setInt(1, noteId);
            preparedStatement.setString(2, editor);

            if(preparedStatement.executeUpdate() < 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
    
    @Override
    public void deleteAll(int noteId) throws DAOException {
        try {
            //Set tham số và thực thi truy vấn
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.DELETE_ALL);
            preparedStatement.setInt(1, noteId);

            if(preparedStatement.executeUpdate() < 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException();
        }
    }
}