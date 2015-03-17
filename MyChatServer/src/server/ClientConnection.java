package server;

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
    private String username;

    private ClientConnectionListener listener;

    //Done
    //TODO maybe rename this class to something like client connection because it may be confusing,
    // it does not represent a client but the server side end of a client communications pipe

    public ClientConnection(final Socket conn) {
        this.conn = conn;

        try {
            oos = new PrintWriter(conn.getOutputStream(), true);
            ois = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            //Done
            //TODO this introduces a high coupling between client and server, server needs to know about client but not vice versa.
            // the connection ID could just as easily have been passed as a constructor parameter

            inputData = parseJSON(ois.readLine());
            username = inputData.get("username").toString();
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
        dispatchMessageToAllClients(username + " just connected.");

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

            int type = Integer.parseInt(inputData.get("type").toString());
            if (type == 1) {
                String msg = username + " disconnected with a LOGOUT message";
                System.out.println(msg);
                dispatchMessageToAllClients(msg);
                break;
            } else {
                String message = inputData.get("message").toString();
                dispatchMessageToAllClients(message);
            }
        }

        close();
        dispatchClientDisconnected();
    }

    //Done
    //TODO synchronisation is a heavy operation, you could try using a writer thread, a blocking queue or something similar.


    //Done
    //TODO i see you have delegated the closing to this class but you are not using it in the Server implementation

    private void close() {
        if (oos != null) {
            oos.close();
        }
        try {
            if ((ois != null) && (!ois.ready())) {
                try {
                    ois.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
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

    private void dispatchMessageToAllClients(final String message) {
        if (listener != null) {
            listener.onPublicMessageListener(message);
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

        public void onPublicMessageListener(String message);
    }

    //Done
    //TODO some of these getters are not necesarry, this is not a java Bean class after all
    public PrintWriter getOos() {
        return oos;
    }
}
