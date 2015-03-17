package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Server implements ClientConnection.ClientConnectionListener {

    private List<ClientConnection> allConnectedClients;
    private int port;
    private ServerSocket serverSocket;
    private BlockingDeque<String> messageQueue;
    private Thread messageSenderThread;

    public Server(int port) {
        this.port = port;
        allConnectedClients = new ArrayList<ClientConnection>();
        messageQueue = new LinkedBlockingDeque<String>();
    }

    public void start() {

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Chat Server waiting for clients on port [" + port + "]");
            while (true) {
                Socket newConnection = serverSocket.accept();
                System.out.println("New client: " + newConnection.getInetAddress());
                ClientConnection client = new ClientConnection(newConnection);
                client.setClientConnectionListener(this);
                allConnectedClients.add(client);
                client.start();
            }

            //Done
            //TODO move the below code into a finally{} block, othervise if an IOEXception
            // occurs while accepting connections the closing of the socket will not take place

            //Done
            //TODO closing the server socket, and each ONE of the client sockets should be in separate
            // try blocks, otherwise an error in closing one would skip the closing of the others

            //Done
            //TODO also you should delegate the closing of the client streams to the client object itself
            // to have good encapsulation

            //Done
            //TODO since the clients are all threads, you may want to Join to the client threads and
            // wait for them to finish before letting the main thread end and causing the process
            // to be flushed from memory before client threads finish.

        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
    }

    private void close() {
        if (!serverSocket.isClosed()) {
            for (ClientConnection client : allConnectedClients) {
                try {
                    client.join();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            try {
                serverSocket.close();
                System.out.println("Chat server stopped.");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    @Override
    public void onClientDisconnected(ClientConnection clientConnection) {
        remove(clientConnection);
    }

    @Override
    public void onDispatchPublicMessage(String message) {
        try {
            messageQueue.put(message);
            if (messageSenderThread == null) {
                messageSenderThread = new Thread(new SenderRunnable());
                messageSenderThread.start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    // for a client who logoff using the LOGOUT message
    private void remove(ClientConnection clientConnection) {
        //Done
        //TODO this is another cross dependency introduced between server and client, to not do
        // this you may want to implement and observer pattern to have the server listen for end of
        // communication events from the clients and have the Server do the removing of the client as needed
        // this is called DEPENDENCY INVERSION and it is an important concept of SOLID architectures
        allConnectedClients.remove(clientConnection);
        System.out.println("client removed from connected clients list");
    }

    private class SenderRunnable implements Runnable{

        @Override
        public void run() {
            while (true) {
                sendMessageToAll();
            }
        }

        private void sendMessageToAll() {
            try {
                String message = messageQueue.take();
                for (ClientConnection clientConnection : allConnectedClients) {
                    clientConnection.sendMessage(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
