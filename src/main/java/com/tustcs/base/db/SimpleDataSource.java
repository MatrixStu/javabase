package com.tustcs.base.db;

import com.tustcs.base.utils.Log;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by yhy on 2017/4/14.
 */
public class SimpleDataSource implements DataSource {
    public static String dirverClassName = "com.mysql.jdbc.Driver";
    public static String url = "jdbc:mysql://localhost:3306/tust_tv?useUnicode=true&characterEncoding=UTF-8";
    public static String user = "root";
    public static String pswd = "";
    private static final int maxNum = 20;
    private static final int minNum = 5;
    private static int conNum = 0;

    //连接池
    private static List<Connection> pool = Collections.synchronizedList(new LinkedList<Connection>());
    private static SimpleDataSource instance = new SimpleDataSource();

    public SimpleDataSource() {
        try {
            Class.forName(dirverClassName);
        } catch (ClassNotFoundException e) {
            Log.d("找不到驱动类！");
        }
    }

    /**
     * 获取数据源单例
     *
     * @return 数据源单例
     */
    public static SimpleDataSource instance() {
        if (instance == null) instance = new SimpleDataSource();
        return instance;
    }

    /**
     * 获取一个数据库连接
     *
     * @return 一个数据库连接
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        synchronized (pool) {
//            if (pool.size() > 0) return pool.remove(0);
//            else {
//                if(conNum > maxNum){
//                    try {
//                        pool.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                conNum++;
//                System.out.println(conNum);
                return makeConnection();
//            }
        }
    }

    /**
     * 连接归池
     *
     * @param conn
     */
    public void free(Connection conn) throws SQLException{
        synchronized (pool) {
            if (pool.size() > maxNum) {
                conn.close();
                conNum --;
                pool.notify();
            }
            else pool.add(conn);
        }
    }

    private Connection makeConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pswd);
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    public void setLoginTimeout(int seconds) throws SQLException {

    }

    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
