package com.example.objectlibdb.service.impl.ex;

public class UsrExistException extends ServiceException{
    public UsrExistException() {
        super();
    }

    public UsrExistException(String message) {
        super(message);
    }

    public UsrExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsrExistException(Throwable cause) {
        super(cause);
    }

    protected UsrExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
