package services;

import db.DatabaseConnection;
import db.Sql;
import utils.JSObject;
import utils.Filter;
import utils.IllegalArgeeMentException;

import java.util.List;

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

    private static Store store ;
    private static boolean debuged = false;
    private static boolean inited = false;
    public static void init(String userName,String userPwd,String dbUrl){
        DatabaseConnection.USERNAME = userName;
        DatabaseConnection.PASSWORD = userPwd;
        DatabaseConnection.URL = dbUrl;
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
}
