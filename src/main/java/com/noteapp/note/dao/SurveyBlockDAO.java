package com.noteapp.note.dao;

import com.noteapp.common.dao.FailedExecuteException;
import com.noteapp.common.dao.DAOException;
import com.noteapp.common.dbconnection.MySQLDatabaseConnection;
import com.noteapp.common.dbconnection.SQLDatabaseConnection;
import com.noteapp.note.model.SurveyBlock;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/**
 * Triển khai các phương thức thao tác CSDL SurveyBlock
 * @author Nhóm 17
 */
public class SurveyBlockDAO implements ISurveyBlockDAO {
    protected SQLDatabaseConnection databaseConnection;
    protected Map<String, String> enableQueries;
    
    protected static final String SQL_FILE_DIR = "src/main/java/com/noteapp/note/db/SurveyBlockQueries.sql";

    protected static final String DATABASE_HOST = "localhost";  
    protected static final int DATABASE_PORT = 3306;
    protected static final String DATABASE_NAME = "notelitedb";
    protected static final String DATABASE_USERNAME = "root";
    protected static final String DATABASE_PASSWORD = "Asensio1234@";

    protected static enum ColumnName {
        block_id, editor, survey_map;
    }
    
    protected static enum QueriesType {
        GET_ALL_BY_BLOCK, CREATE, UPDATE;
    }
    
    private SurveyBlockDAO() {
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
        private static final SurveyBlockDAO INSTANCE = new SurveyBlockDAO();
    }
    
    /**
     * Trả về một thể hiện duy nhất của SurveyBlockDAO
     * @return Thể hiện duy nhất của SurveyBlockDAO
     */
    public static SurveyBlockDAO getInstance() {
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
    public List<SurveyBlock> getAll(int blockId) throws DAOException {
        try {
            //Thực thi truy vấn SQL và lấy kết quả là một bộ dữ liệu
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.GET_ALL_BY_BLOCK);
            preparedStatement.setInt(1, blockId);
            ResultSet resultSet = preparedStatement.executeQuery();
            //Chuyển từng hàng dữ liệu sang textBlock và thêm vào list
            List<SurveyBlock> surveyBlocks = new ArrayList<>();
            while (resultSet.next()) {
                SurveyBlock surveyBlock = new SurveyBlock();
                surveyBlock.setId(resultSet.getInt(ColumnName.block_id.toString()));
                surveyBlock.setEditor(resultSet.getString(ColumnName.editor.toString()));
                JSONObject surveyJSONObject = new JSONObject(resultSet.getString(ColumnName.survey_map.toString()));
                surveyBlock.setSurveyMap(surveyJSONObject);
                surveyBlocks.add(surveyBlock);
            }    
            //Nếu noteBlocks rỗng thì ném ngoại lệ là danh sách trống
            return surveyBlocks;
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new FailedExecuteException(ex.getCause());
        }
    }
    
    @Override
    public void create(SurveyBlock newSurveyBlock) throws DAOException {
        try {
            //Thực thi truy vấn SQL và lấy kết quả là một bộ dữ liệu
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.CREATE);
            preparedStatement.setInt(1, newSurveyBlock.getId());
            preparedStatement.setString(2, newSurveyBlock.getEditor());
            preparedStatement.setString(3, newSurveyBlock.getSurveyJSONObject().toString());
            //Chuyển từng hàng dữ liệu sang textBlock và thêm vào list
            if(preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
    
    @Override
    public void update(SurveyBlock surveyBlock) throws DAOException {
        try {
            //Thực thi truy vấn SQL và lấy kết quả là một bộ dữ liệu
            PreparedStatement preparedStatement = getPrepareStatement(QueriesType.UPDATE);
            preparedStatement.setString(1, surveyBlock.getSurveyJSONObject().toString());
            preparedStatement.setInt(2, surveyBlock.getId());
            preparedStatement.setString(3, surveyBlock.getEditor());

            if(preparedStatement.executeUpdate() <= 0) {
                throw new FailedExecuteException();
            }
        } catch (SQLException ex) {
            throw new FailedExecuteException(ex.getCause());
        }
    }
}