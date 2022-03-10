package com.nduy.realtimechatapp.Utils;

import android.content.Context;

import java.util.HashMap;

public class DBCollectionConstant {
    public final static String User = "user";
    public static final String Message = "message";
    public static final String Conversation = "conversations";
    public static final String UserAvailability = "availability";
    public static HashMap<String, String> remoteMessageHeaders = null;
    public static final String CLOUD_MESSAGING_DATA = "data";
    public static final String CLOUD_MESSAGING_REGISTRATION = "registration_ids";
    public static final String FCM_API = "https://fcm.googleapis.com/fcm/send";

    public static HashMap<String, String> getRemoteMessageHeaders(Context context) {
        if (remoteMessageHeaders == null) {
            remoteMessageHeaders = new HashMap<>();
            remoteMessageHeaders.put(
                    "Authorization",
                    "key=" + Helper.getMetaData(context, "firebase_cloud_messaging_key")
            );
            remoteMessageHeaders.put(
                    "Content-Type",
                    "application/json"
            );
        }
        return remoteMessageHeaders;
    }
}
