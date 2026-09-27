package com.adesidaleye.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080)) {
            Scanner scanner = new Scanner(System.in);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // reads prompt and sends username before starting thread
            String prompt = in.readLine();
            System.out.println(prompt);
            String username = scanner.nextLine();
            out.println(username);

            // separate thread just for listening, so incoming messages can print without waiting for this client message
            Thread listnerThread = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });
            listnerThread.start();

            while (true) {
                // System.out.println("Enter message: ");
                String message = scanner.nextLine();
                out.println(message);

                if (message.equals("/quit")) {
                    break;
                }
            }
        }
        catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            System.out.println("Something went wrong");
        }
    }
}