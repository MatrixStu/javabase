package services;

import junit.framework.TestCase;
import utils.JSObject;

import java.util.List;

/**
 * Created by yhy on 2017/4/15.
 */
public class DBTest extends TestCase {
    String table = "tust_tv_signals";
    public void setUp() throws Exception {
        super.setUp();
        DB.init("root","","jdbc:mysql://localhost:3306/tust_tv?useUnicode=true&characterEncoding=UTF-8");
        DB.setDebuged(true);
    }

    public void testScan() throws Exception {
        List<JSObject> objectList = DB.scan(table)
                .select("*")
                .groupBy("tvId","signalId")
                .orderByDesc("tvId")
                .execute();

        DB.simpleScan(table)
                .select("*")
                .groupBy("tvId")
                .execute();
    }

}