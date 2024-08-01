package com.maktoday.model;

/**
 * Created by cbl81 on 8/11/17.
 */

public class ApiResponse<T> {
    private String success;
    private String message;
    private String statusCode;

    private T data;

    public String getSuccess() {
        return success;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}