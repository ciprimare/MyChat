package com.mychatserver.entity;

import java.io.Serializable;

/**
 * Created by ciprian.mare on 3/20/2015.
 */
public class Client {
    private String username;
    private String password;

    public Client(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
