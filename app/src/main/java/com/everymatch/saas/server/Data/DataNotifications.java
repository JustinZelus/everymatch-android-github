package com.everymatch.saas.server.Data;

/**
 * Created by Dor on 7/27/15.
 */
public class DataNotifications {
    public String _id;
    public String users_id;
    public String message_resource;
    public boolean seen;
    public boolean read;
    public String font_name;
    public String status;
    public String object_id;
    public String collection_name;
    public String notification_type;
    public String channel_name;

    public DataDate updated_date;
    public String updated_by;
    public DataAction actions[];
    public ApiAccess api_access;

    public String url;
    public String message;
    public String image_url;

    public class ApiAccess {
        public String url;
        public urlParameterList url_parameter_list;
        public String url_http_method;

        public class urlParameterList {
            public String app_id;
            public String id;
        }
    }

    public class DataAction {
        public String action_url;
        public ActionParameterList action_parameter_list;
        public String action_http_method;
        public String text;

        public class ActionParameterList {
            public String app_id;
            public String object_id;
            public String action;
            public String other_user_id;
        }

    }
}
