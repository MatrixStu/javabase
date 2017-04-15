package db;

import utils.Log;

import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseConnection {
	
//  public static String USERNAME = "58ba80009be14187b2e7b7c1381a3d2e";
//  //数据库密�?
//  public static String PASSWORD = "9749802ad38c4983aa73d7c48e242f75";
//  //驱动信息
//  public static String DRIVER = "com.mysql.jdbc.Driver";
//  //数据库地�?
//  public static String URL = "jdbc:mysql://sqld.duapp.com:4050/rmTYQKenYWrHgnzXdlHO?useUnicode=true&characterEncoding=UTF-8";
	
	  //数据库用户名
    public static String USERNAME = "root";
    //数据库密�?
    public static String PASSWORD = "";
    //驱动信息
    public static String DRIVER = "com.mysql.jdbc.Driver";
    //数据库地�?
    public static String URL = "jdbc:mysql://localhost:3306/xinglun?useUnicode=true&characterEncoding=UTF-8";
	//数据库用户名  
//    public static String USERNAME = "rlep9028mkzn24p0";
//    //数据库密
//    public static String PASSWORD = "tvzb2016";
//    //驱动信息
//    public static String DRIVER = "com.mysql.jdbc.Driver";
//    //数据库地
//    public static String URL = "jdbc:mysql://rdsxvh0slk45me5q6h60.mysql.rds.aliyuncs.com:3306/rlep9028mkzn24p0?useUnicode=true&characterEncoding=UTF-8";
    private Connection connection;  
    public DatabaseConnection() {
        // TODO Auto-generated constructor stub  
        try{
            Class.forName(DRIVER);
        }catch(Exception e){  
  
        }  
    }  
      
    /** 
     * 获得数据库的连接 
     * @return 
     */  
    public Connection getConnection(){
        try {  
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Log.d("数据库连接成功！");
//        	connection = connPool.getConnection();
        } catch (Exception e) {  
            // TODO Auto-generated catch block
            Log.d("数据库连接失败!");
            e.printStackTrace();  
        }  
        return connection;  
    }  
  
    /** 
     * 释放数据库连
     * @throws Exception 
     */  
    public void releaseConn() throws Exception{  
        if(connection != null ){  
            try{
               connection.close();
            }catch(Exception e){  
                throw e;
            }  
        }  
    }  
}
