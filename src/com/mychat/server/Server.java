package com.mychat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static int uniqueId;
    public static List<ClientThread> allConnectedClients;
    private int port;
    private boolean keepAlive;


    public Server(int port) {
        this.port = port;
        allConnectedClients = new ArrayList<ClientThread>();
    }

    public void start() {
        keepAlive = true;
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (keepAlive) {
                System.out.println("Chat Server waiting for clients on port [" + port + "]");
                Socket newConnection = serverSocket.accept();
                if (!keepAlive) {
                    System.out.println("Chat server stopped.");
                    break;
                }

                ClientThread client = new ClientThread(newConnection);
                allConnectedClients.add(client);
                client.start();
            }

            try {
                serverSocket.close();
                for (ClientThread client : allConnectedClients) {
                    client.getConn().close();
                    client.getOis().close();
                    client.getOos().close();
                }
            } catch (IOException e) {
                //server socket and clients closing exception
            }
        } catch (IOException e) {
            //server socket exception
        }
    }



}
