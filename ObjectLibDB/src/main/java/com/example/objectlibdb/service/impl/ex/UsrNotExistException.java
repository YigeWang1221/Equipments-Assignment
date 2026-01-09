package com.example.objectlibdb.service.impl.ex;

public class UsrNotExistException extends ServiceException{
    public UsrNotExistException() {
        super();
    }

    public UsrNotExistException(String message) {
        super(message);
    }

    public UsrNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsrNotExistException(Throwable cause) {
        super(cause);
    }

    protected UsrNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
