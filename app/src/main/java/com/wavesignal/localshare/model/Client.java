package com.wavesignal.localshare.model;

import java.net.*;
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Client {
    String serverIP;
    boolean isRunning = true;
    UpdateListener listener;
    public Client(String serverIP, UpdateListener listener) {
        this.serverIP = serverIP;
        this.listener = listener;
    }

    public void start() {
        try {
            System.out.println("Connecting to " + serverIP + ":" + Server.PORT);
            while (isRunning) {
                Socket clientSocket = new Socket(serverIP, Server.PORT);

                // Create input and output streams for communication
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Send data to the server
                String message = "Client: Hello, Server!";
                out.println(LocalTime.now().format(DateTimeFormatter.ISO_TIME) + ": " + message);

                // Receive and display the server's response
                String serverResponse = in.readLine();
                System.out.println("Received from server: " + serverResponse);
                listener.onUpdate(serverResponse);

                clientSocket.close();

                // Sleep for a while before the next communication (optional)
                Thread.sleep(1000); // Sleep for 1 second
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

