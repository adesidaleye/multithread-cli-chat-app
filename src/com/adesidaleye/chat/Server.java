package com.adesidaleye.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final List<PrintWriter> clientWriters = new CopyOnWriteArrayList<>();
    private static int clientCount = 0;
    private static final int MAX_CLIENTS = 3;

    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server started on port 8080");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                // reject if full, skip thread creation
                if (clientWriters.size() > MAX_CLIENTS) {
                    PrintWriter tempOut = new PrintWriter(clientSocket.getOutputStream(), true);
                    tempOut.println("Server full. Please try again later.");

                    clientSocket.close();
                    System.out.println("Rejected a connection — server full.");
                    continue;
                }

                clientCount++;
                int clientId = clientCount;

                // create a thread per client for main to accept other clients
                Thread clientThread = new Thread(() -> handleClient(clientSocket, clientId, formatter));
                clientThread.start();
            }

        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket, int clientId, DateTimeFormatter formatter) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriters.add(out);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // create username before chat loop starts
            out.println("Enter your username:");
            String username = in.readLine();
            System.out.println(username + " connected as Client" + clientId);

            String msg;
            while ((msg = in.readLine()) != null) {
                if (msg.equals("/quit")) {
                    System.out.println(username + " disconnected (via /quit)");
                    break;
                }
                String timestamp = LocalTime.now().format(formatter);

                // broadcast to everyone, not just the sender
                System.out.println("Client" + clientId + " sent: " + msg);
                for (PrintWriter writer : clientWriters) {
                    writer.println("[" + timestamp + "] " + username + " says: " + msg);
                }
            }

            // removes current client/sender when /quit is entered
            clientWriters.remove(out);
            for (PrintWriter writer : clientWriters) {
                writer.println(username + " has left chat.");
            }
        } catch (IOException e) {
            System.out.println("Connection closed");
        }
    }
}