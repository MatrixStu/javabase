package com.tustcs.base.services;

import com.tustcs.base.db.SimpleDataSource;
import com.tustcs.base.db.Sql;
import com.tustcs.base.utils.JSObject;
import com.tustcs.base.utils.Filter;
import com.tustcs.base.utils.IllegalArgeeMentException;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

/**
 * Created by yhy on 2016/7/17.
 */
public class DB {
    public static final int COUNT = 1;
    public static final int SUM = 2;
    public static final int MAX = 3;
    public static final int MIN = 4;
    public static final int DISTINCT = 5;
    public static final int AVG = 6;
    public static DataSource dsc = null;

    private static Store store ;
    private static boolean debuged = false;
    private static boolean inited = false;
    public static void init(String userName,String userPwd,String dbUrl){
        SimpleDataSource.user = userName;
        SimpleDataSource.pswd = userPwd;
        SimpleDataSource.url = dbUrl;
        dsc = SimpleDataSource.instance();
        store = new StoreClient();
        inited = true;
    }

    public static void init(String userName,String userPwd,String dbUrl,String driver){
        SimpleDataSource.user = userName;
        SimpleDataSource.pswd = userPwd;
        SimpleDataSource.url = dbUrl;
        SimpleDataSource.dirverClassName = driver;
        dsc = SimpleDataSource.instance();
        store = new StoreClient();
        inited = true;
    }

    public static void init(DataSource dataSource){
        dsc = dataSource;
        store = new StoreClient();
        inited = true;
    }

    public static boolean isDebuged() {
        return debuged;
    }

    public static void setDebuged(boolean debug) {
        DB.debuged = debug;
    }

    public static Store.Insert insert(String table){
        if(!inited){
            throw new IllegalArgeeMentException("must call Db.inited method firtly");
        }
        return store.insert(table);
    }

    public static Store.Delete delete(String table){
        if(!inited){
            throw new IllegalArgeeMentException("must call Db.inited method firtly");
        }
        return store.delete(table);
    }

    public static Store.Update update(String table){
        if(!inited){
            throw new IllegalArgeeMentException("must call Db.inited method firtly");
        }
        return store.update(table);
    }

    public static Store.Scan scan(String table){
        if(!inited){
            throw new IllegalArgeeMentException("must call Db.inited method firtly");
        }
        return store.scan(table);
    }

    public static Store.SimpleScan simpleScan(String table){
        if(!inited){
            throw new IllegalArgeeMentException("must call Db.inited method firtly");
        }
        return store.simpleScan(table);
    }

    public static List<JSObject> findListBySql(String sql, Object ... params) throws Exception{
        if(!inited){
            throw new IllegalArgeeMentException("must call Db.inited method firtly");
        }
        return Sql.findList(sql,params);
    }

    public static JSObject findOneBySql(String sql, Object ... params) throws Exception{
        if(!inited){
            throw new IllegalArgeeMentException("must call Db.inited method firtly");
        }
        return Sql.findOne(sql, params);
    }

    public static boolean updateBySql(String sql,Object ...params) throws Exception{
        if(!inited){
            throw new IllegalArgeeMentException("must call Db.inited method firtly");
        }
        return Sql.update(sql,params);
    }

    public static Filter filter(){
        if(!inited){
            throw new IllegalArgeeMentException("must call Db.inited method firtly");
        }
        return new Filter();
    }

    public static String func(int func,String column){
        if(!inited){
            throw new IllegalArgeeMentException("must call Db.inited method firtly");
        }
        return store.func(func,column);
    }

    /**
     * 因为junit不支持多线程测试,所以用main方法替代之
     * **/
    public static void main(String[] args) throws Exception{
        final long temp = System.currentTimeMillis();
        final String table = "tust_tv_signals";

        Properties prop = new Properties();
        prop.setProperty("driver", "com.mysql.jdbc.Driver");
        prop.setProperty("url", "jdbc:mysql://localhost:3306/tust_tv?useUnicode=true&characterEncoding=UTF-8");
        prop.setProperty("username", "root");
        prop.setProperty("password", "");
        prop.setProperty("initialSize", "3");
        prop.setProperty("maxIdle", "10");
        prop.setProperty("maxActive", "20");
        //144852 6013
        DataSource ds = BasicDataSourceFactory.createDataSource(prop);
//        DB.init("root","","jdbc:mysql://localhost:3306/tust_tv?useUnicode=true&characterEncoding=UTF-8");
        DB.init(ds);
        DB.setDebuged(true);

        for (int i = 0; i < 100; i++) {
            new Thread(){
                @Override
                public void run() {
                    for (int j = 0; j < 100; j++) {
                        try{
                            List<JSObject> objectList = DB.scan(table)
                                    .select("*")
                                    .groupBy("tvId", "signalId")
                                    .orderByDesc("tvId")
                                    .execute();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                    System.out.println("" + (System.currentTimeMillis() - temp));
                }
            }.start();
        }
    }
}
