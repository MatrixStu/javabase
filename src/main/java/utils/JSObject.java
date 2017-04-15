package utils;

import java.util.*;

public class JSObject {
    private HashMap<String, Object> data = new HashMap<String,Object>();
    public static final Object NULL = new JSObject.Null();


    public JSObject() {
    }

    public JSObject(JSTokener x) throws JSException{
        this();
        if(x.nextClean() != 123) {
            throw x.syntaxError("A JSONObject text must begin with \'{\'");
        } else {
            while(true) {
                char c = x.nextClean();
                switch(c) {
                    case '\u0000':
                        throw x.syntaxError("A JSONObject text must end with \'}\'");
                    case '}':
                        return;
                    default:
                        x.back();
                        String key = x.nextValue().toString();
                        c = x.nextClean();
                        if(c == 61) {
                            if(x.next() != 62) {
                                x.back();
                            }
                        } else if(c != 58) {
                            throw x.syntaxError("Expected a \':\' after a key");
                        }

                        this.put(key, x.nextValue());
                        switch(x.nextClean()) {
                            case ',':
                            case ';':
                                if(x.nextClean() == 125) {
                                    return;
                                }

                                x.back();
                                break;
                            case '}':
                                return;
                            default:
                                throw x.syntaxError("Expected a \',\' or \'}\'");
                        }
                }
            }
        }
    }

    public JSObject(String source) throws JSException{
        this(new JSTokener(source));
    }

    private boolean checkType(Object o) {
        return o instanceof String || o instanceof Integer || o instanceof Short || o instanceof Long || o instanceof Double || o instanceof Boolean || o instanceof List || o instanceof JSObject;
    }

    private void ensureType(Object o) {
        if(o == null) {
            throw new IllegalArgumentException("value is empty");
        } else if(!this.checkType(o)) {
            throw new IllegalArgumentException("unsupported type[" + o.getClass().getName() + "]");
        }
    }

    /**
     * if you the value is not exist,might be return null.
     * if you want get a list and this list is not exist,might be return null,you should better use optList
     * **/
    public <T> T get(String key) {
        Object o = this.data.get(key);
        return (T) o;
    }

    private boolean getBoolean(String key) throws JSException {
        Object object = this.get(key);
        if(!object.equals(Boolean.FALSE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("false"))) {
            if(!object.equals(Boolean.TRUE) && (!(object instanceof String) || !((String)object).equalsIgnoreCase("true"))) {
                throw new JSException("JSONObject[" + quote(key) + "] is not a Boolean.");
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private double getDouble(String key) throws JSException {
        Object object = this.get(key);

        try {
            return object instanceof Number?((Number)object).doubleValue():Double.parseDouble((String)object);
        } catch (Exception var4) {
            throw new JSException("JSONObject[" + quote(key) + "] is not a number.");
        }
    }

    private int getInt(String key) throws JSException {
        Object object = this.get(key);

        try {
            return object instanceof Number?((Number)object).intValue():Integer.parseInt((String)object);
        } catch (Exception var4) {
            throw new JSException("JSONObject[" + quote(key) + "] is not an int.");
        }
    }

    private <T> T getList(String key) throws JSException {
        Object object = this.get(key);
        if(object instanceof List) {
            return (T) object;
        } else {
            throw new JSException("object[" + quote(key) + "] is not a List.");
        }
    }

    private JSObject getJSObject(String key) throws JSException {
        Object object = this.get(key);
        if(object instanceof JSObject) {
            return (JSObject)object;
        } else {
            throw new JSException("JSONObject[" + quote(key) + "] is not a JSONObject.");
        }
    }

    private long getLong(String key) throws JSException {
        Object object = this.get(key);

        try {
            return object instanceof Number?((Number)object).longValue():Long.parseLong((String)object);
        } catch (Exception var4) {
            throw new JSException("JSONObject[" + quote(key) + "] is not a long.");
        }
    }

    private <T> T opt(String key) {
        return (T)(key == null?null:this.data.get(key));
    }

    /**
    * the default value is false
    **/
    public boolean optBoolean(String key) {
        return this.optBoolean(key, false);
    }

    public boolean optBoolean(String key, boolean defaultValue) {
        try {
            return this.getBoolean(key);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    public double optDouble(String key) {
        return this.optDouble(key, 0.0D / 0.0);
    }

    public double optDouble(String key, double defaultValue) {
        try {
            return this.getDouble(key);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public int optInt(String key) {
        return this.optInt(key, 0);
    }

    public int optInt(String key, int defaultValue) {
        try {
            return this.getInt(key);
        } catch (Exception var4) {
            return defaultValue;
        }
    }

    /**
     * if the list is null,might be return a new arrayList.
     * **/
    public <T> T optList(String key) {
        Object object = this.get(key);
        if(object instanceof List) {
            return (T) object;
        } else {
            return (T)new ArrayList();
        }
    }

    /**
     * if the JSObject is not exist,might be return null.
     * **/
    public JSObject optJSObject(String key) {
        Object object = this.opt(key);
        return object instanceof JSObject?(JSObject)object:null;
    }

    public long optLong(String key) {
        return this.optLong(key, 0L);
    }

    public long optLong(String key, long defaultValue) {
        try {
            return this.getLong(key);
        } catch (Exception var5) {
            return defaultValue;
        }
    }

    public String optString(String key) {
        return this.optString(key, "");
    }

    public String optString(String key, String defaultValue) {
        Object object = this.opt(key);
        return NULL.equals(object)?defaultValue:object.toString();
    }

    public <T> JSObject put(String key, T value) {
        this.ensureType(value);
        this.data.put(key, value);
        return this;
    }

    public boolean contains(String key) {
        return this.data.containsKey(key);
    }

    public Set<String> getKeys() {
        return this.data.keySet();
    }

    public JSObject add(String key, Object value) {
        this.ensureType(value);
        Object o = this.data.get(key);
        Object as;
        if(o == null) {
            as = new ArrayList();
        } else {
            if(!(o instanceof ArrayList)) {
                throw new IllegalArgumentException("this key is not an arraylist");
            }

            as = (List)o;
        }

        ((List)as).add(value);
        this.data.put(key, as);
        return this;
    }

    public boolean hasObjectData() {
        return !this.data.isEmpty();
    }

    public HashMap<String, Object> getObjectData() {
        return this.data;
    }

    public void setObjectData(HashMap<String, Object> data) {
        this.data = data;
    }

    public void delete(String key) {
        this.data.remove(key);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        Set keys = this.getKeys();
        Iterator iterator = keys.iterator();
        int i = 0;

        while(iterator.hasNext()) {
            String key = (String)iterator.next();
            sb.append("\"" + key + "\":");
            if(this.get(key) == null) {
                sb.append("\"\"");
            } else if(this.get(key) instanceof List) {
                sb.append("[");
                List objects = (List)this.get(key);
                int j = 0;
                Iterator i$ = objects.iterator();

                while(i$.hasNext()) {
                    Object object = i$.next();
                    if(object == null) {
                        sb.append("\"\"");
                    } else if(object instanceof String) {
                        sb.append("\"" + quote(object.toString()) + "\"");
                    } else if(this.checkType(object)) {
                        sb.append(object.toString());
                    }

                    ++j;
                    if(j != objects.size()) {
                        sb.append(",");
                    }
                }

                sb.append("]");
            } else if(this.get(key) instanceof String) {
                sb.append("\"" + quote(this.get(key).toString()) + "\"");
            } else if(this.checkType(this.get(key))) {
                sb.append(this.get(key).toString());
            }

            ++i;
            if(i != keys.size()) {
                sb.append(",");
            }
        }

        sb.append("}");
        return sb.toString();
    }

    public String quote(String string) {
        if(string != null && string.length() != 0) {
            char c = 0;
            int len = string.length();
            StringBuffer sb = new StringBuffer(len + 4);

            for(int i = 0; i < len; ++i) {
                char b = c;
                c = string.charAt(i);
                switch(c) {
                    case '\b':
                        sb.append("\\b");
                        continue;
                    case '\t':
                        sb.append("\\t");
                        continue;
                    case '\n':
                        sb.append("\\n");
                        continue;
                    case '\f':
                        sb.append("\\f");
                        continue;
                    case '\r':
                        sb.append("\\r");
                        continue;
                    case '\"':
                    case '\\':
                        sb.append('\\');
                        sb.append(c);
                        continue;
                    case '/':
                        if(b == 60) {
                            sb.append('\\');
                        }

                        sb.append(c);
                        continue;
                }

                if(c >= 32 && (c < 128 || c >= 160) && (c < 8192 || c >= 8448)) {
                    sb.append(c);
                } else {
                    String hhhh = "000" + Integer.toHexString(c);
                    sb.append("\\u" + hhhh.substring(hhhh.length() - 4));
                }
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    private static final class Null {
        private Null() {
        }

        protected final Object clone() {
            return this;
        }

        public boolean equals(Object object) {
            return object == null || object == this;
        }

        public String toString() {
            return "null";
        }
    }
}