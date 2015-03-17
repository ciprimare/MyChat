package client;


import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartClient {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java StartClient [username] [hostname] [port]");
            System.exit(1);
        }

        String username = args[0];
        String hostname = args[1];
        int port = Integer.parseInt(args[2]);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", username);

        Client client = new Client(hostname, port);
        client.start();
        client.sendMessage(jsonObject);

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        while (true) {

            try {
                System.out.print("CHAT ROOM:");
                String message = stdIn.readLine();
                if (message.equalsIgnoreCase("LOGOUT")) {
                    jsonObject = new JSONObject();
                    jsonObject.put("type", 1);
                    client.sendMessage(jsonObject);
                    break;
                } else {
                    jsonObject = new JSONObject();
                    jsonObject.put("type", 0);
                    jsonObject.put("message", message);
                    client.sendMessage(jsonObject);
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
        client.close();
    }
}
