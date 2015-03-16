package com.mychat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection extends Thread {

    private Socket conn;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private int connId;
    private String username;
    private ChatMessage chatMessage;
    private boolean keepAlive;

    //TODO maybe rename this class to something like client connection because it may be confusing,
    // it does not represent a client but the server side end of a client communications pipe

    public ClientConnection(final Socket conn, final int connId) {
        this.conn = conn;
        this.connId = connId;

        try {
            oos = new ObjectOutputStream(conn.getOutputStream());
            ois = new ObjectInputStream(conn.getInputStream());

            //TODO this introduces a high coupling between client and server, server needs to know about client but not vice versa.
            // the connection ID could just as easily have been passed as a constructor parameter

            username = (String) ois.readObject();
            writeMessage(username + " just connected.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    @Override
    public void run() {
        keepAlive = true;

        //TODO you just keep trying to read forever from a client connection that may have been force closed already
        // you should handle not just the happy flow where client disconnects with LOGOUT,
        // but also the error case where the connection gets broken

        while (keepAlive) {
            try {
                //TODO alltough having ObjectStreams is a very straight-forward way of implementing
                // java - to java socket communication it is not a cross-platform solution and does
                // not provide extendibility in the communications protocol while still providing backwards compatibility
                // these are things you need to consider in real life scenarios and implement something a bit
                // more flexible like a JSON , XML or other standard based communication

                if (!keepAlive){
                    break;
                }

                chatMessage = (ChatMessage) ois.readObject();
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
                break;
            } catch (IOException e) {
                System.out.println(e.getMessage());
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

    //TODO synchronisation is a heavy operation, you could try using a writer thread, a blocking queue or something similar.

    private synchronized void writeMessage(String message) {
        // if Client is still connected send the message to it
        if (!conn.isConnected()) {
            close();
            return;
        }
        // write the message to the stream
        try {
            for (ClientConnection client : Server.allConnectedClients) {
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

        //TODO this is another cross dependency introduced between server and client, to not do
        // this you may want to implement and observer pattern to have the server listen for end of
        // communication events from the clients and have the Server do the removing of the client as needed
        // this is called DEPENDENCY INVERSION and it is an important concept of SOLID architectures

        for (ClientConnection client : Server.allConnectedClients) {
            // found it

            //TODO why are you testing against ID equality, can you not use instance equality and just do
            // allConnectedClients.remove(this) ?;

            if (client.getConnId() == id) {

                //TODO even though this case works ok, because after removing the client you immediately return,
                // you should never ever do remove operations within a foreach cycle, use iterator and iterator.remove() instead

                Server.allConnectedClients.remove(client);
                return;
            }
        }
    }

    //TODO i see you have delegated the closing to this class but you are not using it in the Server implementation

    public void close() {
        try {
            ois.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            keepAlive = false;
        }
        try {
            oos.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            keepAlive = false;
        }
        try {
            ois.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            keepAlive = false;
        }
    }

    //TODO some of these getters are not necesarry, this is not a java Bean class after all

    public int getConnId() {
        return connId;
    }
}
