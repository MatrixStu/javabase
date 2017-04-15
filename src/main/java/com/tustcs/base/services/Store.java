package com.tustcs.base.services;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import com.tustcs.base.utils.JSObject;

import java.util.List;

public abstract class Store {
    public final static int INVALID_TYPE = 0;
    public final static int INT_TYPE = 1;
    public final static int FLOAT_TYPE = 2;
    public final static int BOOL_TYPE = 3;
    public final static int STRING_TYPE = 4;
    public final static int OBJECT_TYPE = 5;
    public final static int ARRAY_TYPE = 6;
    public final static int INVALID_CONNECTOR = 0;
    public final static int AND = 1;
    public final static int OR = 2;
    public final static int WHERE = 3;
    public final static int INVALID_ORDER = 0;
    public final static int ASC = 1;
    public final static int DESC = 2;

    public Store() {

    }

    public abstract String func(int func,String column);

    public abstract Insert insert(String table);

//    public abstract Store.Find find(Object... var1);

    public abstract Store.Delete delete(String table);

//    public abstract Store.BatchDelete batchDelete(Object... var1);

    public abstract Store.Update update(String table);

//    public abstract Store.Replace replace(Object... var1);

    public abstract Store.Scan scan(String table);

//    public abstract Store.FullScan fullScan();

    public abstract SimpleScan simpleScan(String table);

    public interface SimpleScan {
        public SimpleScan select(String... var1);

        public SimpleScan where(JSObject filter);

        public SimpleScan and(JSObject filter);

        public SimpleScan or(JSObject filter);

        public SimpleScan orderByAsc(String... var1);

        public SimpleScan orderByDesc(String... var1);

        public SimpleScan groupBy(String... var1);

//        SimpleScan count();
//
//        SimpleScan sum(String... var1);
//
//        SimpleScan avg(String... var1);
//
//        SimpleScan max(String... var1);
//
//        SimpleScan min(String... var1);

        public JSObject execute() throws Exception;
    }

//    public interface FullScan {
//        Store.FullScan select(String... var1);
//
//        Store.FullScan start(Object... var1) throws Exception;
//
//        Store.FullScan end(Object... var1) throws Exception;
//
//        Store.FullScan limit(int var1);
//
//        Store.FullScan where(ACFilter var1);
//
//        Store.FullScan and(ACFilter var1);
//
//        Store.FullScan or(ACFilter var1);
//
//        Store.FullScan orderByAsc(String... var1);
//
//        Store.FullScan orderByDesc(String... var1);
//
//        Store.FullScan groupBy(String... var1);
//
//        Store.FullScan count();
//
//        Store.FullScan sum(String... var1);
//
//        Store.FullScan avg(String... var1);
//
//        Store.FullScan max(String... var1);
//
//        Store.FullScan min(String... var1);
//
//        ACIterator execute() throws Exception;
//    }

    public interface Scan {
        public Store.Scan select(String... var1);

        public Store.Scan start(int var1);

        public Store.Scan limit(int var1);

        public Store.Scan where(JSObject filter);

        public Store.Scan and(JSObject filter);

        public Store.Scan or(JSObject filter);

        public Store.Scan orderByAsc(String... var1);

        public Store.Scan orderByDesc(String... var1);

        public Store.Scan groupBy(String... var1);

        public List<JSObject> execute() throws Exception;
    }

//    public interface Replace {
//        Store.Replace put(String var1, Object var2);
//
//        void execute() throws Exception;
//    }

    public interface Update {
        public Update put(String var1, Object var2);

        public Update limit(int var1);

        public Update orderByAsc(String... var1);

        public Update orderByDesc(String... var1);

        public Update where(JSObject filter);

        public Update and(JSObject filter);

        public Update or(JSObject filter);

        public boolean execute() throws Exception;
    }

//    public interface BatchDelete {
//        Store.BatchDelete where(ACFilter var1);
//
//        Store.BatchDelete and(ACFilter var1);
//
//        Store.BatchDelete or(ACFilter var1);
//
//        void execute() throws Exception;
//    }

    public interface Delete {
        public Delete orderByAsc(String... var1);

        public Delete orderByDesc(String... var1);

        public Delete limit(int var1);

        public Delete where(JSObject filter);

        public Delete and(JSObject filter);

        public Delete or(JSObject filter);

        boolean execute() throws Exception;
    }

//    public interface Find {
//        Store.Find select(String... var1);
//
//        ACObject execute() throws Exception;
//    }

    public interface Insert {
        public Insert put(String var1, Object var2) throws Exception;

        public boolean execute() throws Exception;
    }

//    public interface Insert {
//        Store.Insert put(String var1, Object var2) throws Exception;
//
//        boolean execute() throws Exception;
//    }
}

