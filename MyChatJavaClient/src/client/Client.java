package client;


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
            oos = new PrintWriter(conn.getOutputStream(), true);
            ois = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            ListenFromServer listenFromServer = new ListenFromServer(ois);
            listenFromServer.start();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

    }

    public void sendMessage(JSONObject jsonObject) {
        oos.println(jsonObject.toJSONString());
    }

    public void close() {
        if (ois != null) {
            try {
                ois.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        if (oos != null) {
            oos.close();
        }
        if (conn.isConnected()) {
            try {
                conn.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }


}