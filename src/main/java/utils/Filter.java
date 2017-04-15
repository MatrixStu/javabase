package utils;

/**
 * Created by yhy on 2016/7/12.
 */
public class Filter {
    public final static int INVALID = 0;
    public final static int EQUAL = 1;
    public final static int NOT_EQUAL = 2;
    public final static int GREATER = 3;
    public final static int GREATER_OR_EQUAL = 4;
    public final static int LESS = 5;
    public final static int LESS_OR_EQUAL = 6;
    public final static int LIKE = 7;
    public static final String KEY = "key";
    public static final String OPERATOR = "operator";
    public static final String VALUE = "value";
    private JSObject filter = new JSObject();

    public Filter() {
    }

    private void checkType(Object o) {
        if(o == null) {
            throw new IllegalArgumentException("value is null");
        }else if((o instanceof Integer)){
            o = Long.valueOf(((Integer) o).longValue());
        }else if((o instanceof Float)){
            o = Double.valueOf(((Float) o).floatValue());
        } else if(!(o instanceof Long) && !(o instanceof Double) && !(o instanceof String) && !(o instanceof Boolean)) {
            throw new IllegalArgumentException("unsupported type[" + o.getClass().getName() + "]");
        }
    }

    private Filter addFilter(String key, Long operator, Object value) {
        this.checkType(value);
//        JSObject zo = new JSObject();
        filter.put("key", key);
        filter.put("operator", operator);
        filter.put("value", value);
        return this;
    }

    public JSObject whereEqualTo(String key, Object value) {
        this.addFilter(key, Long.valueOf(EQUAL), value);
        return getFilter();
    }

    public JSObject whereNotEqualTo(String key, Object value) {
        this.addFilter(key, Long.valueOf(NOT_EQUAL), value);
        return getFilter();
    }

    public JSObject whereGreaterThan(String key, Object value) {
        this.addFilter(key, Long.valueOf(GREATER), value);
        return getFilter();
    }

    public JSObject whereGreaterThanOrEqualTo(String key, Object value) {
        this.addFilter(key, Long.valueOf(GREATER_OR_EQUAL), value);
        return getFilter();
    }

    public JSObject whereLessThan(String key, Object value) {
        this.addFilter(key, Long.valueOf(LESS), value);
        return getFilter();
    }

    public JSObject whereLessThanOrEqualTo(String key, Object value) {
        this.addFilter(key, Long.valueOf(LESS_OR_EQUAL), value);
        return getFilter();
    }

    public JSObject whereLikeTo(String key, Object value) {
        this.addFilter(key, Long.valueOf(LIKE), value);
        return getFilter();
    }

    public JSObject getFilter() {
        return this.filter;
    }
}
