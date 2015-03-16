package com.mychat.server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection extends Thread {

    private Socket conn;
    private PrintWriter oos;
    private BufferedReader ois;
    private int connId;
    private JSONObject inputData;
    private String username;

    //TODO maybe rename this class to something like client connection because it may be confusing,
    // it does not represent a client but the server side end of a client communications pipe

    public ClientConnection(final Socket conn, final int connId) {
        this.conn = conn;
        this.connId = connId;

        try {
            oos = new PrintWriter(conn.getOutputStream(), true);
            ois = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            //TODO this introduces a high coupling between client and server, server needs to know about client but not vice versa.
            // the connection ID could just as easily have been passed as a constructor parameter

            inputData = parseJSON(ois.readLine());
            username = inputData.get("username").toString();
            writeMessage(username + " just connected.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    @Override
    public void run() {
        //TODO you just keep trying to read forever from a client connection that may have been force closed already
        // you should handle not just the happy flow where client disconnects with LOGOUT,
        // but also the error case where the connection gets broken

        while (true) {
            try {
                //TODO alltough having ObjectStreams is a very straight-forward way of implementing
                // java - to java socket communication it is not a cross-platform solution and does
                // not provide extendibility in the communications protocol while still providing backwards compatibility
                // these are things you need to consider in real life scenarios and implement something a bit
                // more flexible like a JSON , XML or other standard based communication

                inputData = parseJSON(ois.readLine());
            } catch (IOException e) {
                System.out.println(e.getMessage());
                break;
            }

            int type = Integer.parseInt(inputData.get("type").toString());
            if (type == 1) {
                String msg = username + " disconnected with a LOGOUT message";
                System.out.println(msg);
                writeMessage(msg);
                break;
            } else {
                String message = inputData.get("message").toString();
                writeMessage(message);
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
        for (ClientConnection client : Server.allConnectedClients) {
            if (client != this) {
                client.oos.println(message);
            }
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

    private void close() {
        if (oos != null) {
            oos.close();
        }
        if (ois != null) {
            try {
                ois.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        if (conn.isConnected()) {
            try {
                conn.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private JSONObject parseJSON(String jsonString) {
        JSONParser parser = new JSONParser();
        JSONObject result = null;
        try {
            result = (JSONObject) parser.parse(jsonString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return result;
    }

    //TODO some of these getters are not necesarry, this is not a java Bean class after all

    public int getConnId() {
        return connId;
    }
}
