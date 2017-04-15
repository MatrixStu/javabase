package utils;


import static services.DB.*;

/**
 * Created by yhy on 2016/7/14.
 */
public class Func {
    public String func(int fun,String column){
        if (column == null || column.length() <= 0){
            throw new IllegalArgeeMentException("column can not be null or empty!");
        }
        switch ((int)fun){
            case COUNT:
                return "count("+column+")";
            case SUM:
                return "sum("+column+")";
            case MAX:
                return "max("+column+")";
            case MIN:
                return "min("+column+")";
            case DISTINCT:
                return "distinct("+column+")";
            case AVG:
                return "avg("+column+")";
            default:throw new IllegalArgeeMentException("no function found");
        }
    }
}
