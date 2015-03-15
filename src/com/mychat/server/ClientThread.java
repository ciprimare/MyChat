package com.mychat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread {

    private Socket conn;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private int connId;
    private String username;
    private ChatMessage chatMessage;
    private String date;


    public ClientThread(final Socket conn) {
        this.conn = conn;

        try {
            oos = new ObjectOutputStream(conn.getOutputStream());
            ois = new ObjectInputStream(conn.getInputStream());
            connId = ++Server.uniqueId;
            username = (String) ois.readObject();
            writeMessage(username + " just connected.");
        } catch (IOException e) {
            //exception creating object input and output streams
            return;
        } catch (ClassNotFoundException e) {
            //class not found exceptions
        }
    }

    @Override
    public void run() {
        boolean keepAlive = true;

        while (keepAlive) {
            try {
                chatMessage = (ChatMessage) ois.readObject();
            } catch (ClassNotFoundException e) {
                //class not found exception
                break;
            } catch (IOException e) {
                //exception reading streams
                break;
            }

            String message = chatMessage.getMessage();

            switch (chatMessage.getType()) {
                case MESSAGE:
                    writeMessage(message);
                    break;
                case LOGOUT:
                    String msg = username + " disconnected with a LOGOUT message";
                    System.out.println(msg);
                    writeMessage(msg);
                    keepAlive = false;
                    break;
            }
        }


        //remove my id from connected list clients and close
        remove(getConnId());
        close();
    }

    private synchronized void writeMessage(String message) {
        // if Client is still connected send the message to it
        if (!conn.isConnected()) {
            close();
            return;
        }
        // write the message to the stream
        try {
            for (ClientThread client : Server.allConnectedClients) {
                if (client != this) {
                    client.oos.writeObject(message);
                }
            }
        } catch (IOException e) {
            // exception output stream
        }
    }

    // for a client who logoff using the LOGOUT message
    private synchronized static void remove(int id) {
        // scan the array list until we found the Id
        for (ClientThread client : Server.allConnectedClients) {
            // found it
            if (client.getConnId() == id) {
                Server.allConnectedClients.remove(client);
                return;
            }
        }
    }

    private void close() {
        try {
            oos.close();
            ois.close();
            conn.close();
        } catch (IOException e) {
            // exception closing streams and connection
        }
    }

    public Socket getConn() {
        return conn;
    }

    public ObjectOutputStream getOos() {
        return oos;
    }

    public ObjectInputStream getOis() {
        return ois;
    }

    public int getConnId() {
        return connId;
    }

    public String getUsername() {
        return username;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public String getDate() {
        return date;
    }
}
