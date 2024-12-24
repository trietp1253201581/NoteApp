package com.noteapp.note.dao;

import com.noteapp.dao.DAOKeyException;
import com.noteapp.dao.NotExistDataException;
import com.noteapp.dao.FailedExecuteException;
import com.noteapp.dao.DAOException;
import com.noteapp.dao.connection.MySQLDatabaseConnection;
import com.noteapp.dao.sql.SQLReader;
import com.noteapp.note.model.Note;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Triển khai các phương thức thao tác CSDL Note
 * @author Nhóm 17
 */
public class NoteDAO extends AbstractNoteDAO implements INoteDAO {
    
    protected static final String SQL_FILE_DIR = "src/main/java/com/noteapp/note/db/NoteQueries.sql";

    protected static enum ColumnName {
        note_id, author, header, last_modified_date, is_public; 
    }

    protected static enum QueriesType {
        GET_ALL_BY_AUTHOR, GET, CREATE, UPDATE, DELETE;
    }

    /**
     * Khởi tạo và lấy connection tới Database
     */
    private NoteDAO() {
        //init connection
        setDatabaseConnection(new MySQLDatabaseConnection(DATABASE_HOST, DATABASE_PORT, 
            DATABASE_NAME, DATABASE_USERNAME, DATABASE_PASSWORD));
        initConnection();
        //get enable query
        setFileReader(new SQLReader());
        getEnableQueriesFrom(SQL_FILE_DIR);
    }

    private static class SingletonHelper {
        private static final NoteDAO INSTANCE = new NoteDAO();
    }    
    
    /**
     * Lấy thể hiện duy nhất của lớp này
     * @return Instance duy nhất
     */
    public static NoteDAO getInstance() {
        return SingletonHelper.INSTANCE;
    }

    @Override
    public List<Note> getAll(String author) throws DAOException {
        //Kiểm tra key
        if("".equals(author)) {
            throw new DAOKeyException();
        }

        try {
            //Set các tham số, thực thi truy vấn và lấy kết quả
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.GET_ALL_BY_AUTHOR.toString());
            preparedStatement.setString(1, author);
            
            ResultSet resultSet = preparedStatement.executeQuery();
            //Duyệt các hàng kết quả
            List<Note> notes = new ArrayList<>();
            while (resultSet.next()) {
                Note note = new Note();
                //Set dữ liệu từ hàng vào note
                note.setId(resultSet.getInt(ColumnName.note_id.toString()));
                note.setAuthor(resultSet.getString(ColumnName.author.toString()));
                note.setHeader(resultSet.getString(ColumnName.header.toString()));
                note.setLastModifiedDate(resultSet.getDate(ColumnName.last_modified_date.toString()));
                note.setPubliced(Boolean.parseBoolean(resultSet.getString(ColumnName.is_public.toString())));
                //Thêm note vào list
                notes.add(note);
            }
            return notes;
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }       
    }

    @Override
    public Note get(int noteId) throws DAOException {
        try {
            //Set các tham số, thực thi truy vấn và lấy kết quả
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.GET.toString());
            preparedStatement.setInt(1, noteId);

            ResultSet resultSet = preparedStatement.executeQuery();
            Note note = new Note();
            while (resultSet.next()) {
                //Set dữ liệu từ kết quả vào note
                note.setId(resultSet.getInt(ColumnName.note_id.toString()));
                note.setAuthor(resultSet.getString(ColumnName.author.toString()));
                note.setHeader(resultSet.getString(ColumnName.header.toString()));
                note.setLastModifiedDate(resultSet.getDate(ColumnName.last_modified_date.toString()));
                note.setPubliced(Boolean.parseBoolean(resultSet.getString(ColumnName.is_public.toString())));
            }
            if(note.isDefaultValue()) {
                throw new NotExistDataException();
            }
            return note;
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }

    @Override
    public Note create(Note newNote) throws DAOException {
        try {
            //Set tham số và thực thi truy vấn
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.CREATE.toString(), 
                    PreparedStatement.RETURN_GENERATED_KEYS);   
            preparedStatement.setString(1, newNote.getAuthor());
            preparedStatement.setString(2, newNote.getHeader());
            preparedStatement.setDate(3, newNote.getLastModifiedDate());
            preparedStatement.setString(4, String.valueOf(newNote.isPubliced()));
            
            if(preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while(resultSet.next()) {
                newNote.setId(resultSet.getInt(1));
            }
            return newNote;
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }

    @Override
    public void update(Note note) throws DAOException {
        try {
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.UPDATE.toString());            
            preparedStatement.setString(1, note.getAuthor());
            preparedStatement.setString(2, note.getHeader());
            preparedStatement.setDate(3, note.getLastModifiedDate());
            preparedStatement.setString(4, String.valueOf(note.isPubliced()));
            preparedStatement.setInt(5, note.getId());
            
            if(preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
    
    @Override
    public void delete(int noteId) throws DAOException {      
        try {
            //Set tham số và thực thi truy vấn
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.DELETE.toString());
            preparedStatement.setInt(1, noteId);
            
            if(preparedStatement.executeUpdate() < 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
}