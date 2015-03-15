package com.mychat.server;

import java.io.Serializable;


public class ChatMessage implements Serializable {

    private static final long serialVersionUID = 1112122200L;
    private Op type;
    private String message;


    public ChatMessage(Op type, String message) {
        this.type = type;
        this.message = message;
    }

    public Op getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
