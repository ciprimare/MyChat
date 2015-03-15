package com.mychat.client;


import com.mychat.server.ChatMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private String username;
    private String hostname;
    private int port;
    private Socket conn;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public Client(String username, String hostname, int port) {
        this.username = username;
        this.hostname = hostname;
        this.port = port;
    }

    public void start() {
        try {
            conn = new Socket(hostname, port);
            System.out.println("Connection accepted on " + hostname + " and port " + port);
            oos = new ObjectOutputStream(conn.getOutputStream());
            ois = new ObjectInputStream(conn.getInputStream());

            new ListenFromServer(ois, username).start();
            oos.writeObject(username);
        } catch (IOException e) {
            //conn and streams exceptions
            disconnect();
            return;
        }

    }

    public void sendMessage(ChatMessage chatMessage) {
        try {
            oos.writeObject(chatMessage);
        } catch (IOException e) {
            // output stream exception
        }
    }

    public void disconnect() {
        try {
            ois.close();
            oos.close();
            conn.close();
        } catch (IOException e) {
            //conn and streams exceptions
        }

    }


}