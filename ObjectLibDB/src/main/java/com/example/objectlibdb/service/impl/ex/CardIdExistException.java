package com.example.objectlibdb.service.impl.ex;

public class CardIdExistException extends ServiceException{
    public CardIdExistException() {
        super();
    }

    public CardIdExistException(String message) {
        super(message);
    }

    public CardIdExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardIdExistException(Throwable cause) {
        super(cause);
    }

    protected CardIdExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
