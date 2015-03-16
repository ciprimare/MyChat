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

            //TODO move the below code into a finally{} block, othervise if an IOEXception
            // occurs while accepting connections the closing of the socket will not take place

            //TODO closing the server socket, and each ONE of the client sockets should be in separate
            // try blocks, otherwise an error in closing one would skip the closing of the others

            //TODO also you should delegate the closing of the client streams to the client object itself
            // to have good encapsulation

            //TODO since the clients are all threads, you may want to Join to the client threads and
            // wait for them to finish before letting the main thread end and causing the process
            // to be flushed from memory before client threads finish.
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
