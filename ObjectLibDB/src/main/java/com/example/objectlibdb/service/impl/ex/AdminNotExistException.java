package com.example.objectlibdb.service.impl.ex;

public class AdminNotExistException extends ServiceException{
    public AdminNotExistException() {
        super();
    }

    public AdminNotExistException(String message) {
        super(message);
    }

    public AdminNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdminNotExistException(Throwable cause) {
        super(cause);
    }

    protected AdminNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
