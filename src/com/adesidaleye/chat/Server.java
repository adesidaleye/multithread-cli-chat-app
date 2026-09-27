package com.adesidaleye.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server started on port 8080");

            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println("Client sent: " + msg);

                    out.println("Server received: " + msg);
                }
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
