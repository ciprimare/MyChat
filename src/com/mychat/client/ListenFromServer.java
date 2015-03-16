package com.mychat.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ListenFromServer extends Thread {

    private BufferedReader ois;

    ListenFromServer(final BufferedReader ois){
        this.ois = ois;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg =  ois.readLine();
                System.out.println(msg);
                System.out.print("CHAT ROOM:");
            } catch (IOException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
    }
}
