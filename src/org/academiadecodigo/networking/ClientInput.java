package org.academiadecodigo.networking;

import java.io.*;
import java.net.Socket;

public class ClientInput implements Runnable{

    private BufferedReader in;
    private PrintWriter out;
    private Thread thread;

    public ClientInput(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            String user = in.readLine();
            System.out.println(user);
            while (true) {
                String msg = in.readLine();
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
