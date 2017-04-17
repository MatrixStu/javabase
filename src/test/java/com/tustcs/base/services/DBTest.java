package com.tustcs.base.services;

import junit.framework.TestCase;
import com.tustcs.base.utils.JSObject;
import org.apache.commons.dbcp2.BasicDataSourceFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;

/**
 * Created by yhy on 2017/4/15.
 */
public class DBTest extends TestCase {
    String table = "tust_tv_signals";

    public void setUp() throws Exception {
        super.setUp();
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
    }

    public void testScan() throws Exception {
        for (int j = 0; j < 100; j++) {
            try {
                List<JSObject> objectList = DB.scan(table)
                        .select("*")
                        .groupBy("tvId", "signalId")
                        .orderByDesc("tvId")
                        .execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}