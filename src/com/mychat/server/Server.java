package com.mychat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private int uniqueId;
    public static List<ClientConnection> allConnectedClients;
    private int port;
    private boolean keepAlive;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
        allConnectedClients = new ArrayList<ClientConnection>();
    }

    public void start() {
        keepAlive = true;

        try {
            serverSocket = new ServerSocket(port);
            while (keepAlive) {
                System.out.println("Chat Server waiting for clients on port [" + port + "]");
                Socket newConnection = serverSocket.accept();
                if (!keepAlive) {
                    System.out.println("Chat server stopped.");
                    break;
                }

                ClientConnection client = new ClientConnection(newConnection, ++uniqueId);
                allConnectedClients.add(client);
                client.start();
                client.join();
            }

            //TODO move the below code into a finally{} block, othervise if an IOEXception
            // occurs while accepting connections the closing of the socket will not take place

            //TODO closing the server socket, and each ONE of the client sockets should be in separate
            // try blocks, otherwise an error in closing one would skip the closing of the others

            //TODO also you should delegate the closing of the client streams to the client object itself
            // to have good encapsulation

            //TODO since the clients are all threads, you may want to Join to the client threads and
            // wait for them to finish before letting the main thread end and causing the process
            // to be flushed from memory before client threads finish.

        } catch (InterruptedException e) {
            keepAlive = false;
            System.out.println(e.getMessage());
        } catch (IOException e) {
            keepAlive = false;
            System.out.println(e.getMessage());
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                keepAlive = false;
                System.out.println(e.getMessage());
            }
            for (ClientConnection client : allConnectedClients) {
                client.close();
            }
        }
    }
}
