package com.nduy.realtimechatapp.Model;

import java.io.Serializable;
import java.util.Date;

public class ChatMessage implements Serializable {
    private String senderID, receiverID, dateTime, message;
    private Date dateObj;
    private String conversionID, conversionName, conversionImage;
    public static final String SENDER_ID = "sendID";
    public static final String RECEIVED_ID = "receivedID";
    public static final String TIME_STAMP = "timestamp";
    public static final String MESSAGE = "message";
    public static final String SENDER_NAME = "senderName";
    public static final String RECEIVER_NAME = "receiverName";
    public static final String SENDER_IMAGE = "senderImage";
    public static final String RECEIVER_IMAGE = "receiverImage";
    public static final String LAST_MESSAGE = "lastMessage";

    public ChatMessage() {
    }

    public ChatMessage(String sendID, String receivedID, String dateTime, String message) {
        this.senderID = sendID;
        this.receiverID = receivedID;
        this.dateTime = dateTime;
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDateObj() {
        return dateObj;
    }

    public void setDateObj(Date dateObj) {
        this.dateObj = dateObj;
    }

    public String getConversionID() {
        return conversionID;
    }

    public void setConversionID(String conversionID) {
        this.conversionID = conversionID;
    }

    public String getConversionName() {
        return conversionName;
    }

    public void setConversionName(String conversionName) {
        this.conversionName = conversionName;
    }

    public String getConversionImage() {
        return conversionImage;
    }

    public void setConversionImage(String conversionImage) {
        this.conversionImage = conversionImage;
    }
}
