package com.mychat.client;


import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private String hostname;
    private int port;
    private Socket conn;
    private BufferedReader ois;
    private PrintWriter oos;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void start() {
        try {
            conn = new Socket(hostname, port);
            System.out.println("Connection accepted on " + hostname + " and port " + port);
            oos = new PrintWriter(conn.getOutputStream(),true);
            ois = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            new ListenFromServer(ois).start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            disconnect();
            return;
        }

    }

    public void sendMessage(JSONObject jsonObject) {
        oos.println(jsonObject.toJSONString());
    }

    public void disconnect() {
        try {
            conn.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        try {
            ois.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        oos.close();

    }


}