package com.mychat.client;


import java.io.IOException;
import java.io.ObjectInputStream;

public class ListenFromServer extends Thread {

    private ObjectInputStream ois;
    private String username;

    ListenFromServer(final ObjectInputStream ois, final String username){
        this.ois = ois;
        this.username = username;
    }

    @Override
    public void run() {
        boolean keepAlive = true;
        while (keepAlive) {
            try {
                String msg = (String) ois.readObject();
                System.out.println(msg);
                System.out.print("CHAT ROOM:");
            } catch (IOException e) {
                //reading stream connection
                break;
            }
            catch (ClassNotFoundException e) {
                //class not found exception
            }
        }
    }
}
