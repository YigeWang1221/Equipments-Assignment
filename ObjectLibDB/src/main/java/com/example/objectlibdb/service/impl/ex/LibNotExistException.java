package com.example.objectlibdb.service.impl.ex;

public class LibNotExistException extends ServiceException {
    public LibNotExistException() {
        super();
    }

    public LibNotExistException(String message) {
        super(message);
    }

    public LibNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public LibNotExistException(Throwable cause) {
        super(cause);
    }

    protected LibNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
