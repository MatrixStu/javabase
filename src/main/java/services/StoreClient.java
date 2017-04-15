package services;

import db.Sql;
import utils.*;

import java.util.*;

/**
 * Created by yhy on 2016/7/12.
 */
public class StoreClient extends Store {
//    private static final Logger logger = Logger.getLogger(StoreClient.class);
    //    private ACConfiguration config;
//    private ACClient client;
//    private ACContext context;

//    public StoreClient(ACConfiguration config, ACClient client, String className, ACContext context) {
//        this.config = config;
//        this.client = client;
//        this.context = context;
//        this.className = className;
//    }

    public String func(int func, String column) {
        return new Func().func(func,column);
    }

    public Store.Insert insert(String table) {
        if (table == null || table.length() <= 0) {
            throw new IllegalArgeeMentException("table name must not be null or empty");
        }
        Insert insert = new Insert(table);

        return insert;
    }

    public Store.Delete delete(String table) {
        if (table == null || table.length() <= 0) {
            throw new IllegalArgeeMentException("table name must not be null or empty");
        }

        StoreClient.Delete delete = new StoreClient.Delete(table);
        return delete;
    }


    public Store.Update update(String table) {
        if (table == null || table.length() <= 0) {
            throw new IllegalArgeeMentException("table name must not be null or empty");
        }

        StoreClient.Update update = new StoreClient.Update(table);
        return update;
    }

    public Store.Scan scan(String table) {
        if (table == null || table.length() <= 0) {
            throw new IllegalArgeeMentException("table name must not be null or empty");
        }

        StoreClient.Scan scan = new StoreClient.Scan(table);
        return scan;
    }

    public Store.SimpleScan simpleScan(String table) {
        if (table == null || table.length() <= 0) {
            throw new IllegalArgeeMentException("table name must not be null or empty");
        }

        StoreClient.SimpleScan simpleScan = new StoreClient.SimpleScan(table);
        return simpleScan;
    }


    private boolean parseACObjectKVs(JSObject zo, boolean canEmpty, Object... kvs) {
        if (kvs.length % 2 == 0 && (canEmpty || kvs.length != 0)) {
            for (int i = 0; i < kvs.length - 1; i += 2) {
                Object key = kvs[i];
                Object value = kvs[i + 1];
                if (key == null || !(key instanceof String) || !(value instanceof String) && !(value instanceof Integer) && !(value instanceof Long) && !(value instanceof Float) && !(value instanceof Double) && !(value instanceof Boolean)) {
                    Log.d("wrong parameter type. key[" + key + "] key_type[" + key.getClass().getName() + "] value[" + value + "] value_type[" + value.getClass().getName() + "]");
                    return false;
                }

                zo.put((String) key, value);
            }

            return true;
        } else {
            Log.d("invalid parameters.");
            return false;
        }
    }

    public class SimpleScan implements Store.SimpleScan {
        private JSObject object;
        private JSObject filterObject;
        private String table = "";
        private String sql = "";
        private boolean whereFlag = false;
        private Map<String, Long> orderByMap;
        private List<Object> values;
        private List<String> selects;

        public SimpleScan(String table) {
            this.object = new JSObject();
            this.filterObject = new JSObject();
            this.values = new ArrayList<Object>();
            this.orderByMap = new HashMap<String, Long>();
            this.table = table;
            this.selects = new ArrayList<String>();
        }

        public Store.SimpleScan groupBy(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                this.object.add("groupBy", key);
            }

            return this;
        }

        public Store.SimpleScan select(String... var1) {
            for(int i = 0; i < var1.length; i++){
                this.selects.add(var1[i]);
            }
            return this;
        }

        public Store.SimpleScan where(JSObject filter) {
            if (filter != null &&filter.get("key") != null) {
                if (this.whereFlag) {
                    throw new IllegalArgumentException("where clause already exist");
                } else {
                    this.whereFlag = true;
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.WHERE));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgumentException("invalid filter");
            }
        }

        public Store.SimpleScan and(JSObject filter) {
            if (filter != null && filter.get("key") != null) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                } else {
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.AND));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgeeMentException("invalid filter");
            }
        }

        public Store.SimpleScan or(JSObject filter) {
            if (filter != null && filter.get("key") != null) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                } else {
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.OR));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgeeMentException("invalid filter");
            }
        }

        public Store.SimpleScan orderByAsc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != Store.ASC) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(Store.ASC));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(Store.ASC));
                }
            }
            return this;
        }

        public Store.SimpleScan orderByDesc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != Store.DESC) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(Store.DESC));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(Store.DESC));
                }
            }

            return this;
        }

        public JSObject execute() throws Exception {
            if(selects.size() <= 0){
                throw new IllegalArgeeMentException("select column must not be empty");
            }
            sql += "select";
            int seCount = 0;
            for (String se:selects){
                seCount++;
                sql += (" "+ se + (seCount >= selects.size() ? "":","));
            }
            sql += (" from" + " " + table);

            if (this.whereFlag) {
//                if(filterObject.get("filters") == null){
//                    throw new IllegalArgeeMentException("filter is null");
//                    return false;
//                }
                List<JSObject> filters = filterObject.get("filters");
                if (filters != null && filters.size() > 0) {
                    for (JSObject filter : filters) {
                        long con = filter.get("connector");
                        switch ((int) con) {
                            case Store.WHERE: {
                                JSObject fi = filter.get("filter");
                                sql += (" where " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                            case Store.AND: {
                                JSObject fi = filter.get("filter");
                                sql += (" and " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                            case Store.OR: {
                                JSObject fi = filter.get("filter");
                                sql += (" or " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                        }
                    }
                }
            }

            List<String> groupBys = object.get("groupBy");
            if (groupBys != null && groupBys.size() > 0) {
                sql += " group by";
                int count = 0;
                for (String crObject : groupBys) {
                    count++;
                    sql += (" " + crObject + (count >= groupBys.size() ? "" : ","));
                }
            }

            List<JSObject> orderBys = filterObject.get("orderBy");
            if (orderBys != null && orderBys.size() > 0) {
                sql += " order by";
                int count = 0;
                for (JSObject JSObject : orderBys) {
                    count++;
                    sql += (" " + JSObject.get("key") + " " + getOrder((Long) JSObject.get("order")) + (count >= orderBys.size() ? "" : ","));
                }
            }

            sql += " limit 1";

            int size = values.size();
            Object valuesArray[] = new Object[size];
            for (int i = 0; i < size; i++) {
                valuesArray[i] = values.get(i);
            }
            Log.d(sql);
            return Sql.findOne(sql,valuesArray);
        }
    }

    public class Scan implements Store.Scan {
        private JSObject object;
        private JSObject filterObject;
        private String table = "";
        private String sql = "";
        private JSObject flagNumber;
        private boolean limitFlag = false;
        private boolean startFlag = false;
        private boolean whereFlag = false;
        private Map<String, Long> orderByMap;
        private List<Object> values;
        private List<String> selects;

        public Scan(String table) {
            this.object = new JSObject();
            this.filterObject = new JSObject();
            this.flagNumber = new JSObject();
            this.values = new ArrayList<Object>();
            this.orderByMap = new HashMap<String, Long>();
            this.table = table;
            this.selects = new ArrayList<String>();
        }

        public Store.Scan groupBy(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                this.object.add("groupBy", key);
            }

            return this;
        }

        public Store.Scan select(String... var1) {
            for(int i = 0; i < var1.length; i++){
                this.selects.add(var1[i]);
            }
            return this;
        }

        public Store.Scan start(int var1) {
            if(!limitFlag){
                throw new IllegalArgeeMentException("limit must be called first");
            }
            this.startFlag = true;
            this.flagNumber.put("start", Long.valueOf(var1));
            return this;
        }

        public Store.Scan limit(int number) {
            this.limitFlag = true;
            this.flagNumber.put("limit", Long.valueOf(number));
            return this;
        }

        public Store.Scan where(JSObject filter) {
            if (filter != null &&filter.get("key") != null) {
                if (this.whereFlag) {
                    throw new IllegalArgumentException("where clause already exist");
                } else {
                    this.whereFlag = true;
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.WHERE));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgumentException("invalid filter");
            }
        }

        public Store.Scan and(JSObject filter) {
            if (filter != null && filter.get("key") != null) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                } else {
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.AND));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgeeMentException("invalid filter");
            }
        }

        public Store.Scan or(JSObject filter) {
            if (filter != null && filter.get("key") != null) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                } else {
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.OR));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgeeMentException("invalid filter");
            }
        }

        public Store.Scan orderByAsc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != Store.ASC) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(Store.ASC));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(Store.ASC));
                }
            }
            return this;
        }

        public Store.Scan orderByDesc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != Store.DESC) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(Store.DESC));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(Store.DESC));
                }
            }

            return this;
        }

        public List<JSObject> execute() throws Exception {
            if(selects.size() <= 0){
                throw new IllegalArgeeMentException("select column must not be empty");
            }
            sql += "select";
            int seCount = 0;
            for (String se:selects){
                seCount++;
                sql += (" "+ se + (seCount >= selects.size() ? "":","));
            }
            sql += (" from" + " " + table);

            if (this.whereFlag) {
//                if(filterObject.get("filters") == null){
//                    throw new IllegalArgeeMentException("filter is null");
//                    return false;
//                }
                List<JSObject> filters = filterObject.get("filters");
                if (filters != null && filters.size() > 0) {
                    for (JSObject filter : filters) {
                        long con = filter.get("connector");
                        switch ((int) con) {
                            case Store.WHERE: {
                                JSObject fi = filter.get("filter");
                                sql += (" where " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                            case Store.AND: {
                                JSObject fi = filter.get("filter");
                                sql += (" and " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                            case Store.OR: {
                                JSObject fi = filter.get("filter");
                                sql += (" or " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                        }
                    }
                }
            }

            List<String> groupBys = this.object.get("groupBy");
            if (groupBys != null && groupBys.size() > 0) {
                sql += " group by";
                int count = 0;
                for (String crObject : groupBys) {
                    count++;
                    sql += (" " + crObject + (count >= groupBys.size() ? "" : ","));
                }
            }

            List<JSObject> orderBys = filterObject.get("orderBy");
            if (orderBys != null && orderBys.size() > 0) {
                sql += " order by";
                int count = 0;
                for (JSObject JSObject : orderBys) {
                    count++;
                    sql += (" " + JSObject.get("key") + " " + getOrder((Long) JSObject.get("order")) + (count >= orderBys.size() ? "" : ","));
                }
            }


            if (limitFlag) {
                if(startFlag){
                    sql += " limit " + flagNumber.get("start") + "," + flagNumber.get("limit");
                }else{
                    sql += " limit " + flagNumber.get("limit");
                }
            }

            int size = values.size();
            Object valuesArray[] = new Object[size];
            for (int i = 0; i < size; i++) {
                valuesArray[i] = values.get(i);
            }
            Log.d(sql);
            return Sql.findList(sql,valuesArray);
        }
    }

    public class Update implements Store.Update {
        private JSObject object;
        private JSObject filterObject;
        private String table = "";
        private String sql = "";
        private JSObject flagNumber;
        private boolean limitFlag = false;
        private boolean whereFlag = false;
        private Map<String, Long> orderByMap;
        private List<Object> values;

        public Update(String table) {
            this.object = new JSObject();
            this.filterObject = new JSObject();
            this.flagNumber = new JSObject();
            this.values = new ArrayList<Object>();
            this.orderByMap = new HashMap<String, Long>();
            this.table = table;
        }

        public Store.Update put(String var1, Object var2) {
            object.put(var1,var2);
            return this;
        }

        public Store.Update limit(int number) {
            this.limitFlag = true;
            this.flagNumber.put("limit", Long.valueOf(number));
            return this;
        }

        public Store.Update where(JSObject filter) {
            if (filter != null && filter.get("key") != null) {
                if (this.whereFlag) {
                    throw new IllegalArgumentException("where clause already exist");
                } else {
                    this.whereFlag = true;
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.WHERE));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgumentException("invalid filter");
            }
        }

        public Store.Update and(JSObject filter) {
            if (filter != null && filter.get("key") != null) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                } else {
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.AND));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgeeMentException("invalid filter");
            }
        }

        public Store.Update or(JSObject filter) {
            if (filter != null && filter.get("key") != null) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                } else {
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.OR));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgeeMentException("invalid filter");
            }
        }

        public Store.Update orderByAsc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != Store.ASC) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(Store.ASC));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(Store.ASC));
                }
            }
            return this;
        }

        public Store.Update orderByDesc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != Store.DESC) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(Store.DESC));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(Store.DESC));
                }
            }

            return this;
        }

        public boolean execute() throws Exception {
            sql += "update ";
            sql += (table);

            Set<String> keys = this.object.getKeys();
            int keyCount = 0;
            if(keys.size() > 0){
                sql += " set";
                for (String key : keys) {
                    keyCount++;
                    sql += " ";
                    sql += key;
                    sql += (keyCount == keys.size() ? "=?" : "=?,");
                }
            }
            for (String key : keys) {
                values.add(object.get(key));
            }

            if (this.whereFlag) {
//                if(filterObject.get("filters") == null){
//                    throw new IllegalArgeeMentException("filter is null");
//                    return false;
//                }
                List<JSObject> filters = filterObject.get("filters");
                if (filters != null && filters.size() > 0) {
                    for (JSObject filter : filters) {
                        long con = filter.get("connector");
                        switch ((int) con) {
                            case Store.WHERE: {
                                JSObject fi = filter.get("filter");
                                sql += (" where " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                            case Store.AND: {
                                JSObject fi = filter.get("filter");
                                sql += (" and " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                            case Store.OR: {
                                JSObject fi = filter.get("filter");
                                sql += (" or " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                        }
                    }
                }

            }

            List<JSObject> orderBys = filterObject.get("orderBy");
            if (orderBys != null && orderBys.size() > 0) {
                sql += " order by";
                int count = 0;
                for (JSObject JSObject : orderBys) {
                    count++;
                    sql += (" " + JSObject.get("key") + " " + getOrder((Long) JSObject.get("order")) + (count >= orderBys.size() ? "" : ","));
                }
            }

            if (limitFlag) {
                sql += " limit " + flagNumber.get("limit");
            }

            int size = values.size();
            Object valuesArray[] = new Object[size];
            for (int i = 0; i < size; i++) {
                valuesArray[i] = values.get(i);
            }
            Log.d(sql);
            return Sql.update(sql,valuesArray);
        }
    }

    public class Delete implements Store.Delete {
        private JSObject filterObject;
        private String table = "";
        private String sql = "";
        private JSObject flagNumber;
        private boolean limitFlag = false;
        private boolean whereFlag = false;
        private Map<String, Long> orderByMap;
        private List<Object> values;

        public Delete(String table) {
            this.filterObject = new JSObject();
            this.flagNumber = new JSObject();
            this.values = new ArrayList<Object>();
            this.orderByMap = new HashMap<String, Long>();
            this.table = table;
        }

        public Store.Delete limit(int number) {
            this.limitFlag = true;
            this.flagNumber.put("limit", Long.valueOf(number));
            return this;
        }

        public Store.Delete where(JSObject filter) {
            if (filter != null && filter.get("key") != null) {
                if (this.whereFlag) {
                    throw new IllegalArgumentException("where clause already exist");
                } else {
                    this.whereFlag = true;
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.WHERE));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgumentException("invalid filter");
            }
        }

        public Store.Delete and(JSObject filter) {
            if (filter != null && filter.get("key") != null) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                } else {
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.AND));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgeeMentException("invalid filter");
            }
        }

        public Store.Delete or(JSObject filter) {
            if (filter != null && filter.get("key") != null) {
                if (!this.whereFlag) {
                    throw new IllegalArgeeMentException("where clause not found");
                } else {
                    JSObject zo = new JSObject();
                    zo.put("connector", Long.valueOf(Store.OR));
                    zo.put("filter", filter);
                    this.filterObject.add("filters", zo);
                    return this;
                }
            } else {
                throw new IllegalArgeeMentException("invalid filter");
            }
        }

        public Store.Delete orderByAsc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != Store.ASC) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(Store.ASC));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(Store.ASC));
                }
            }
            return this;
        }

        public Store.Delete orderByDesc(String... keys) {
            String[] arr$ = keys;
            int len$ = keys.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                if (this.orderByMap.containsKey(key)) {
                    if (((Long) this.orderByMap.get(key)).longValue() != Store.DESC) {
                        throw new IllegalArgeeMentException("conflict order on same key");
                    }
                } else {
                    JSObject zo = new JSObject();
                    zo.put("key", key);
                    zo.put("order", Long.valueOf(Store.DESC));
                    this.filterObject.add("orderBy", zo);
                    this.orderByMap.put(key, Long.valueOf(Store.DESC));
                }
            }

            return this;
        }

        public boolean execute() throws Exception {
            sql += "delete from ";
            sql += (table);
            if (this.whereFlag) {
//                if(filterObject.get("filters") == null){
//                    throw new IllegalArgeeMentException("filter is null");
//                    return false;
//                }
                List<JSObject> filters = filterObject.get("filters");
                if (filters != null && filters.size() > 0) {
                    for (JSObject filter : filters) {
                        long con = filter.get("connector");
                        switch ((int) con) {
                            case Store.WHERE: {
                                JSObject fi = filter.get("filter");
                                sql += (" where " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                            case Store.AND: {
                                JSObject fi = filter.get("filter");
                                sql += (" and " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                            case Store.OR: {
                                JSObject fi = filter.get("filter");
                                sql += (" or " + fi.get("key") + " " + getOp((Long) fi.get("operator")) + " ?");
                                values.add(fi.get("value"));
                            }
                            break;

                        }
                    }
                }

            }

            List<JSObject> orderBys = filterObject.get("orderBy");
            if (orderBys != null && orderBys.size() > 0) {
                sql += " order by";
                int count = 0;
                for (JSObject JSObject : orderBys) {
                    count++;
                    sql += (" " + JSObject.get("key") + " " + getOrder((Long) JSObject.get("order")) + (count >= orderBys.size() ? "" : ","));
                }
            }

            if (limitFlag) {
                sql += " limit " + flagNumber.get("limit");
            }

            int size = values.size();
            Object valuesArray[] = new Object[size];
            for (int i = 0; i < size; i++) {
                valuesArray[i] = values.get(i);
            }
            Log.d(sql);
            return Sql.update(sql,valuesArray);
        }

    }

    private String getOrder(long i) {
        switch ((int) i) {
            case Store.ASC:
                return "asc";
            case Store.DESC:
                return "desc";
            default:
                return "desc";
        }
    }

    public String getOp(long i) {
        switch ((int) i) {
            case Filter.EQUAL:
                return "=";
            case Filter.NOT_EQUAL:
                return "!=";
            case Filter.GREATER:
                return ">";
            case Filter.GREATER_OR_EQUAL:
                return ">=";
            case Filter.LESS:
                return "<";
            case Filter.LESS_OR_EQUAL:
                return "<=";
            case Filter.LIKE:
                return "like";
            default:
                return "=";
        }
    }

    public class Insert implements Store.Insert {
        private JSObject object;
        private String sql = "";
        private String table = "";

        public Insert(String table) {
            this.table = table;
            object = new JSObject();
        }

        public Store.Insert put(String key, Object value) {
            this.object.put(key, value);
            return this;
        }

        public boolean execute() throws Exception {
            sql = "insert into " + table + " (";
            Set<String> keys = this.object.getKeys();
            int count = 0;
            for (String key : keys) {
                count++;
                sql += key;
                sql += (count == keys.size() ? ")" : ",");
            }
            count = 0;
            sql += " values (";
            Object[] objects = new Object[keys.size()];
            for (String key : keys) {
                objects[count] = object.get(key);
                count++;
                sql += count == keys.size() ? "?)" : "?,";
            }
            Log.d(sql);
            return Sql.update(sql, objects);
        }
    }

}
