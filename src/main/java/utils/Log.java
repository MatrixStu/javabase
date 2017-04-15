package utils;

import services.DB;

/**
 * Created by yhy on 2016/7/17.
 */
public class Log {
    public static void d(String message){
        if(DB.isDebuged())
        System.out.println(message);
    }
}
