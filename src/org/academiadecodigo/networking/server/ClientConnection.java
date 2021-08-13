package org.academiadecodigo.networking.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ClientConnection implements Runnable {
    private Socket clientConnSock;
    private BufferedReader in;
    private PrintWriter out;
    private Thread threadConn;
    private ChatServer chatServer;
    private String username;

    ClientConnection(Socket clientConnSock, ChatServer chatServer) throws IOException {
        this.clientConnSock = clientConnSock;
        out = new PrintWriter(clientConnSock.getOutputStream(), true);
        this.chatServer = chatServer;
        in = new BufferedReader(new InputStreamReader(clientConnSock.getInputStream()));
        setUsername();
        threadConn = new Thread(this);
        threadConn.start();
    }


    @Override
    public void run() {
        try {
            while (true) {
                String msg = in.readLine();
                String[] breakMsg = msg.split(" ");

                if (breakMsg[0].equals("/w")) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 2; i < breakMsg.length; i++) {
                        stringBuilder.append(breakMsg[i] + " ");
                    }
                    String msgOut = stringBuilder.toString();
                    if (!chatServer.serverWhisper(username + ": " + breakMsg[1], msgOut)) {
                        out.println("User not found.");
                    }
                    continue;
                } else {
                    switch (msg) {
                        case "/quit":
                            chatServer.serverBroadcast(username + " has quit.");
                            chatServer.getOutOfTheList(this);
                            getClientConnSock().close();
                            return;
                        case "/username":
                            changeUsername();
                            break;
                        case "/users":
                            out.println(chatServer.users());
                            break;
                        case "/help":
                            out.println("/help  -  asks for help.");
                            out.println("/username  -  asks for a new username and changes yours to it.");
                            out.println("/users");
                            out.println("/w <username> <message>  -  sends a private message to that user.");
                            out.println("/quit  -  quits the chatroom.");
                            break;
                        default:
                            chatServer.serverBroadcast(username + ": " + msg);
                            break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getClientConnSock() {
        return clientConnSock;
    }

    public void send(String s) {
        out.println(s);
    }

    public void setUsername() throws IOException {
        out.print("username: ");
        out.flush();
        username = in.readLine();
        out.println("Use /help for commands.");
        chatServer.serverBroadcast(username + " joined the chatroom.");
    }

    public void changeUsername() throws IOException {
        out.print("username: ");
        out.flush();
        String tempUsername = in.readLine();
        chatServer.serverBroadcast(username + " changed username to " + tempUsername);
        username = tempUsername;
    }

    public String getUsername() {
        return username;
    }


}
