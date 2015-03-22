package com.mychatserver.server;

import com.mychatserver.db.UserDao;
import com.mychatserver.entity.PublicMessage;
import com.mychatserver.entity.User;
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
    private JSONObject inputData;
    private int clientId;

    private ClientConnectionListener listener;

    //Done
    //TODO maybe rename this class to something like client connection because it may be confusing,
    // it does not represent a client but the server side end of a client communications pipe

    public ClientConnection(final Socket conn, int clientId) {
        this.conn = conn;
        this.clientId = clientId;
        try {
            oos = new PrintWriter(conn.getOutputStream(), true);
            ois = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            //Done
            //TODO this introduces a high coupling between client and server, server needs to know about client but not vice versa.
            // the connection ID could just as easily have been passed as a constructor parameter

        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
    }

    @Override
    public void run() {
        //Done
        //TODO you just keep trying to read forever from a client connection that may have been force closed already
        // you should handle not just the happy flow where client disconnects with LOGOUT,
        // but also the error case where the connection gets broken

        while (true) {
            try {
                //Done
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

            if (inputData.containsKey("msgType")) {
                int messageType = Integer.parseInt(inputData.get("msgType").toString());
                User user = new User(inputData.get("username").toString(), inputData.get("password").toString());
                switch (messageType) {
                    case 1:
                        oos.println(new UserDao().registerUser(user));
                        break;
                    case 2:
                        oos.println(new UserDao().authenticate(user));
                        break;
                    case 3:
                        String message = inputData.get("message").toString();
                        dispatchPublicMessage(new PublicMessage(clientId, user.getUsername() + ":" + message));
                        break;
                }
            }

        }

        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        dispatchClientDisconnected();
    }

    public void sendMessage(String message) {
        oos.println(message);
    }

    //Done
    //TODO synchronisation is a heavy operation, you could try using a writer thread, a blocking queue or something similar.


    //Done
    //TODO i see you have delegated the closing to this class but you are not using it in the com.mychatserver.server.Server implementation

    private void close() throws IOException {
        conn.close();
    }

    private JSONObject parseJSON(String jsonString) {
        JSONParser parser = new JSONParser();
        JSONObject result = new JSONObject();
        if (jsonString != null && !jsonString.isEmpty()) {
            try {
                result = (JSONObject) parser.parse(jsonString);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
        }
        return result;
    }

    private void dispatchPublicMessage(final PublicMessage publicMessage) {
        if (listener != null) {
            listener.onDispatchPublicMessage(publicMessage);
        }
    }

    private void dispatchClientDisconnected() {
        if (listener != null) {
            listener.onClientDisconnected(this);
        }
    }

    public void setClientConnectionListener(ClientConnectionListener listener) {
        this.listener = listener;
    }

    public interface ClientConnectionListener {
        public void onClientDisconnected(ClientConnection clientConnection);

        public void onDispatchPublicMessage(PublicMessage publicMessage);
    }

    //Done
    //TODO some of these getters are not necesarry, this is not a java Bean class after all


    public int getClientId() {
        return clientId;
    }
}
