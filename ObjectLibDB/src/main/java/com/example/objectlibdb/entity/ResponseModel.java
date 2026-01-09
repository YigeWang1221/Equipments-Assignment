package com.example.objectlibdb.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResponseModel {
    private int code;
    private String message;
    private Object data;
    private int signInType;

    public ResponseModel(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseModel(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseModel(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public ResponseModel(int code, String message, String data, int signInType) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.signInType=signInType;

    }

    @Override
    public String toString() {
        return "{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
