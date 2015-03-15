package com.mychat.server;

public class StartServer {
    public static void main(String[] args) {

        if(args.length < 1){
            System.out.println("Usage: java StartServer [port] number");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        Server server = new Server(port);
        server.start();
    }
}
