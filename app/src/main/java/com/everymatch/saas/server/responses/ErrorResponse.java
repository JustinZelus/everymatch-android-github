package com.everymatch.saas.server.responses;

import com.android.volley.VolleyError;

public class ErrorResponse extends VolleyError {

    private int statusCode;

    private String serverRawResponse;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getServerRawResponse() {
        return serverRawResponse;
    }

    public void setServerRawResponse(String serverRawResponse) {
        this.serverRawResponse = serverRawResponse;
    }


/*
        @SerializedName("readyState")
        int readyState;
        @SerializedName("responseText")
        String responseText;
        @SerializedName("responseJSON")
        ResponseJSON responseJSON;
        @SerializedName("status")
        int status;
        @SerializedName("statusText")
        String statusText;

        public class ResponseJSON {
            @SerializedName("Message")
            public String message;
            @SerializedName("ModelState")
            public ModelState modelState;

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public ModelState getModelState() {
                return modelState;
            }

            public void setModelState(ModelState modelState) {
                this.modelState = modelState;
            }
        }

        public class ModelState {
            @SerializedName("model.Email")
            String[] modelEmail;

            @SerializedName("model.FirstName")
            String[] modelFirstName;

            @SerializedName("model.LastName")
            String[] modelLastName;

            public String[] getModelEmail() {
                return modelEmail;
            }

            public void setModelEmail(String[] modelEmail) {
                this.modelEmail = modelEmail;
            }

            public String[] getModelFirstName() {
                return modelFirstName;
            }

            public void setModelFirstName(String[] modelFirstName) {
                this.modelFirstName = modelFirstName;
            }

            public String[] getModelLastName() {
                return modelLastName;
            }

            public void setModelLastName(String[] modelLastName) {
                this.modelLastName = modelLastName;
            }
        }

        public int getReadyState() {
            return readyState;
        }

        public void setReadyState(int readyState) {
            this.readyState = readyState;
        }

        public String getResponseText() {
            return responseText;
        }

        public void setResponseText(String responseText) {
            this.responseText = responseText;
        }

        public ResponseJSON getResponseJSON() {
            return responseJSON;
        }

        public void setResponseJSON(ResponseJSON responseJSON) {
            this.responseJSON = responseJSON;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatusText() {
            return statusText;
        }

        public void setStatusText(String statusText) {
            this.statusText = statusText;
        }*/

}
