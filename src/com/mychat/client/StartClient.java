package com.mychat.client;


import com.mychat.server.ChatMessage;
import com.mychat.server.Op;

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

        Client client = new Client(username, hostname, port);
        client.start();

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        boolean keepAlive = true;
        while (keepAlive) {

            try {
                System.out.print("CHAT ROOM:");
                String message = stdIn.readLine();
                if (message.equalsIgnoreCase("LOGOUT")) {
                    client.sendMessage(new ChatMessage(Op.LOGOUT, ""));
                    break;
                } else {
                    client.sendMessage(new ChatMessage(Op.MESSAGE, message));
                }
            } catch (IOException e) {
                //reading line exception
            }
        }
        client.disconnect();
    }
}
