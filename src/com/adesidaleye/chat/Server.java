package com.adesidaleye.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static List<PrintWriter> clientWriters = new CopyOnWriteArrayList<>();
    private static int clientCount = 0;

    public static List<PrintWriter> getClientWriters() {
        return clientWriters;
    }

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server started on port 8080");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientCount++;
                int clientId = clientCount;
                System.out.println("New client connected: Client" + clientId);

                Thread clientThread = new Thread(() -> {
                    try {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        clientWriters.add(out);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                        String msg;
                        while ((msg = in.readLine()) != null) {
                            System.out.println("Client" + clientId + " sent: " + msg);
                            for (PrintWriter writer : clientWriters) {
                                writer.println("Client" + clientId + " says: " + msg);
                            }

                            // out.println("Server received: " + msg);
                        }
                    } catch (IOException e) {
                        System.out.println("Connection closed");
                    }
                });
                clientThread.start();
            }

        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
