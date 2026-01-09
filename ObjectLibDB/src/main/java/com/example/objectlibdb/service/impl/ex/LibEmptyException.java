package com.example.objectlibdb.service.impl.ex;

public class LibEmptyException extends ServiceException{
    public LibEmptyException() {
        super();
    }

    public LibEmptyException(String message) {
        super(message);
    }

    public LibEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public LibEmptyException(Throwable cause) {
        super(cause);
    }

    protected LibEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

