package com.mychatserver.entity;

/**
 * Created by ciprian.mare on 3/22/2015.
 */
public class PublicMessage {
    private int senderId;
    private String message;

    public PublicMessage(int clientId, String message) {
        this.senderId = clientId;
        this.message = message;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getMessage() {
        return message;
    }
}
