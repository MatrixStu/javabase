package db;



import utils.JSObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class DaoImpl implements IDao {
	private Connection connection = null;

    private PreparedStatement pstmt;  
    private ResultSet resultSet;  
    
    public DaoImpl(Connection con){
    	this.connection = con;
    }
    

	public List<JSObject> findModeResult(String con, Object... params)
			throws Exception {
		List<JSObject> list = new ArrayList<JSObject>();
        int index = 1;  
        this.pstmt = connection.prepareStatement(con);  
        if(params != null && params.length > 0){
            for(int i = 0; i<params.length; i++){
                pstmt.setObject(index++, params[i]);
            }  
        }  
        resultSet = pstmt.executeQuery();  
        ResultSetMetaData metaData = resultSet.getMetaData();  
        int cols_len = metaData.getColumnCount();  
        while(resultSet.next()){  
        	JSObject map = new JSObject();
            for(int i=0; i<cols_len; i++){  
                String cols_name = metaData.getColumnName(i+1);  
                Object cols_value = resultSet.getObject(cols_name);  
                if(cols_value == null){  
                    cols_value = "";  
                }  
                map.put(cols_name, cols_value);  
            }  
            list.add(map);  
        }
        if(this.pstmt != null){
            this.pstmt.close();
        }
        if(this.resultSet != null)
            this.resultSet.close();
  
        return list;  
	}


	public JSObject findSimpleResult(String con, Object... params)
			throws Exception {
		JSObject map = new JSObject();
        int index  = 1;  
        pstmt = connection.prepareStatement(con);  
        if(params != null && params.length > 0){
            for(int i=0; i<params.length; i++){
                pstmt.setObject(index++, params[i]);
            }  
        }  
        resultSet = pstmt.executeQuery();//返回查询结果  
        ResultSetMetaData metaData = resultSet.getMetaData();  
        int col_len = metaData.getColumnCount();  
        while(resultSet.next()){  
            for(int i=0; i<col_len; i++ ){  
                String cols_name = metaData.getColumnName(i+1);  
                Object cols_value = resultSet.getObject(cols_name);  
                if(cols_value == null){  
                    cols_value = "";  
                }  
                map.put(cols_name, cols_value);  
            }  
        }
        if(this.pstmt != null){
            this.pstmt.close();
        }
        if(this.resultSet != null)
        this.resultSet.close();
        return map;  
	}


	public boolean updateByPreparedStatement(String con, Object... params)
			throws Exception {
		    boolean flag = false;
	        int result = -1;  
	        pstmt = connection.prepareStatement(con);  
	        int index = 1;  
	        if(params != null && params.length > 0){
	            for(int i=0; i<params.length; i++){
	                pstmt.setObject(index++, params[i]);
	            }  
	        }  
	        result = pstmt.executeUpdate();  
	        flag = result > 0 ? true : false;
            if(pstmt != null){
                this.pstmt.close();
            }
	        return flag;  
	}
}
