package com.noteapp.note.dao;

import com.noteapp.dao.FailedExecuteException;
import com.noteapp.dao.DAOException;
import com.noteapp.dao.connection.MySQLDatabaseConnection;
import com.noteapp.dao.sql.SQLReader;
import com.noteapp.note.model.NoteFilter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai các phương thức thao tác với CSDL NoteFilter
 * @author Nhóm 17
 */
public class NoteFilterDAO extends AbstractNoteDAO implements INoteFilterDAO {
    
    protected static final String SQL_FILE_DIR = "src/main/java/com/noteapp/note/db/NoteFilterQueries.sql";

    protected static enum ColumnName {
        note_id, filter;
    }
    
    protected static enum QueriesType {
        GET_ALL_BY_NOTE, CREATE, DELETE_ALL;
    }

    /**
     * Khởi tạo và lấy connection tới Database
     */
    private NoteFilterDAO() {
        //init connection
        setDatabaseConnection(new MySQLDatabaseConnection(DATABASE_HOST, DATABASE_PORT, 
            DATABASE_NAME, DATABASE_USERNAME, DATABASE_PASSWORD));
        initConnection();
        //get enable query
        setFileReader(new SQLReader());
        getEnableQueriesFrom(SQL_FILE_DIR);
    }

    private static class SingletonHelper {
        private static final NoteFilterDAO INSTANCE = new NoteFilterDAO();
    }    
    
    /**
     * Lấy thể hiện duy nhất của lớp này
     * @return Instance duy nhất
     */
    public static NoteFilterDAO getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override 
    public List<NoteFilter> getAll(int noteId) throws DAOException {    
        try {
            //Thực thi truy vấn SQL và lấy kết quả là một bộ dữ liệu
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.GET_ALL_BY_NOTE.toString());
            preparedStatement.setInt(1, noteId);
            ResultSet resultSet = preparedStatement.executeQuery();
            //Chuyển từng hàng dữ liệu sang noteFilter và thêm vào list
            List<NoteFilter> noteFilters = new ArrayList<>();
            while (resultSet.next()) {
                NoteFilter noteFilter = new NoteFilter();
                //Set dữ liệu cho noteFilter
                noteFilter.setFilter(resultSet.getString(ColumnName.filter.toString()));
                noteFilters.add(noteFilter);
            }    
            
            return noteFilters;
        }  catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
    
    @Override
    public void create(int noteId, NoteFilter newNoteFilter) throws DAOException {
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.CREATE.toString());
            //Set các tham số cho truy vấn
            preparedStatement.setInt(1, noteId);
            preparedStatement.setString(2, newNoteFilter.getFilter());
            
            if(preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
 
    @Override
    public void deleteAll(int noteId) throws DAOException {
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.DELETE_ALL.toString());
            //Set các tham số cho truy vấn
            preparedStatement.setInt(1, noteId);

            if(preparedStatement.executeUpdate() < 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
}