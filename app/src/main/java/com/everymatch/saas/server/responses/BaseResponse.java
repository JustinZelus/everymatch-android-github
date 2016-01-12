package com.everymatch.saas.server.responses;

public class BaseResponse {

    public String status;

    private int httpStatus;

    public static final int ERROR_UNKNOWN = 1234;

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public String getStatus() {
        return status;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }
}