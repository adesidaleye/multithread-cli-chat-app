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
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader server = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread listnerThread = new Thread(() -> {
                try {
                    String msg;
                    while ((msg = server.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });
            listnerThread.start();

            while (true) {
                System.out.print("Enter message: ");
                out.println(scanner.nextLine());
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
