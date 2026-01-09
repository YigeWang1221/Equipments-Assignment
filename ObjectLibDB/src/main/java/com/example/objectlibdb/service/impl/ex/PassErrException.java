package com.example.objectlibdb.service.impl.ex;

public class PassErrException extends ServiceException{
    public PassErrException() {
        super();
    }

    public PassErrException(String message) {
        super(message);
    }

    public PassErrException(String message, Throwable cause) {
        super(message, cause);
    }

    public PassErrException(Throwable cause) {
        super(cause);
    }

    protected PassErrException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
