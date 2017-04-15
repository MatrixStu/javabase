package com.tustcs.base.utils;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yhy on 2017/4/15.
 */
public class JSObjectTest extends TestCase {
    public void testGetKeys() throws Exception {
        List<JSObject> objectList = new ArrayList<JSObject>();
        JSObject config = new JSObject();
        for (int i = 0; i < 10; i++) {
            JSObject jsObject = new JSObject();
            jsObject.put("userName","test")
                    .put("userPass","test");
            objectList.add(jsObject);
        }

        config.put("status",1)
                .put("dataList",objectList)
                .put("msg","'测试\n[国]");

        System.out.println(config);
        JSObject test = new JSObject(config.toString());
        List<JSObject> s = test.optList("test");
        System.out.println(s.size());
        System.out.println(test.get("a"));
//        System.out.println(test.optString("msg"));
    }

}