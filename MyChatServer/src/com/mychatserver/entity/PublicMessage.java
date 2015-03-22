package com.mychatserver.entity;

/**
 * Created by cc on 3/22/2015.
 */
public class PublicMessage {
    private int clientId;
    private String message;

    public PublicMessage(int clientId, String message) {
        this.clientId = clientId;
        this.message = message;
    }

    public int getClientId() {
        return clientId;
    }

    public String getMessage() {
        return message;
    }
}
