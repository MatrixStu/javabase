package com.tustcs.base.utils;

/**
 * Created by yhy on 2017/4/15.
 */
public class JSException extends Exception {
    private static final long serialVersionUID = 0L;
    private Throwable cause;

    public JSException(String message) {
        super(message);
    }

    public JSException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }
}

