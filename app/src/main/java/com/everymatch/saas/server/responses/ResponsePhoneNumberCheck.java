package com.everymatch.saas.server.responses;

import java.io.Serializable;

/**
 * Created by PopApp_laptop on 18/02/2016.
 */
public class ResponsePhoneNumberCheck extends BaseResponse implements Serializable {
    public boolean is_new_user;

    public String getMessage() {
        if (message == null) {
            message = "";
        }
        return message;
    }

    public String getErrorMessage() {
        return getSms_results().getErrorMessage();
    }

    private String message;
    public boolean success;

    public DataSmsResults getSms_results() {
        if (sms_results == null)
            sms_results = new DataSmsResults();
        return sms_results;
    }

    private DataSmsResults sms_results;


    public class DataSmsResults implements Serializable {
        public int ErrorCode;

        public String getErrorMessage() {
            if (ErrorMessage == null)
                ErrorMessage = "";
            return ErrorMessage;
        }

        public String ErrorMessage;
        public String RestException;
        public boolean success;

    }
}
