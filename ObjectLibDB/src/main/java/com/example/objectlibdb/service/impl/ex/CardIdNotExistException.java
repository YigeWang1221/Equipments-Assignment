package com.example.objectlibdb.service.impl.ex;

public class CardIdNotExistException extends ServiceException{
    public CardIdNotExistException() {
        super();
    }

    public CardIdNotExistException(String message) {
        super(message);
    }

    public CardIdNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CardIdNotExistException(Throwable cause) {
        super(cause);
    }

    protected CardIdNotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
