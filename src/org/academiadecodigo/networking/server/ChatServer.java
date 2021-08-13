package org.academiadecodigo.networking.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * ChatServer
 */
public class ChatServer {

    public static final int PORT = 9999;
    private Queue<ClientConnection> clientConnQ;
    private ServerSocket serverSocket;

    public ChatServer(int port) {

        try {

            clientConnQ = new LinkedList<>();
            // bind the socket to specified port
            System.out.println("Binding to port " + port);
            serverSocket = new ServerSocket(port);

            System.out.println("Server started: " + serverSocket);

            // block waiting for a client to connect
            System.out.println("Waiting for a client connection");


        } catch (IOException ioe) {

            System.out.println(ioe.getMessage());

        }

    }

    public void listen() {
        try {
            Socket connSocket = serverSocket.accept();
            clientConnQ.offer(new ClientConnection(connSocket, this));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serverBroadcast(String s) {
        for (ClientConnection ccq : clientConnQ) {
            ccq.send(s);
        }
        System.out.println(s);
    }

    public String users() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ClientConnection ccq : clientConnQ) {
            stringBuilder.append(ccq.getUsername() + " ");
        }
        return stringBuilder.toString();
    }

    public boolean serverWhisper(String w, String s) {
        int count = 0;
        for (ClientConnection ccq : clientConnQ) {
            if (ccq.getUsername().equals(w)) {
                ccq.send(s);
                count++;
            }
        }
        System.out.println(s);
        if (count == 0) {
            return false;
        }
        return true;
    }

    /**
     * ChatServer entry point
     *
     * @param args ChatServer port number
     */
    public static void main(String args[]) {


        try {
            // try to create an instance of the ChatServer at port specified at args[0]
            ChatServer cs = new ChatServer(PORT);

            while (true) {
                cs.listen();
            }

        } catch (NumberFormatException ex) {
            // write an error message if an invalid port was specified by the user
            System.out.println("Invalid port number " + args[0]);
        }

    }


    public void getOutOfTheList(ClientConnection clientConnection) {
        clientConnQ.remove(clientConnection);
    }
}
