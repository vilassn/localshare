package com.wavesignal.localshare.model;

import java.net.*;
import java.io.*;
import java.time.LocalTime;

public class Server {
    public static int PORT = 12345;
    boolean isRunning = true;
    UpdateListener listener;

    public Server(UpdateListener listener) {
        this.listener = listener;
    }

    public void start() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);

            while (isRunning) {
                System.out.println("Server is listening on port " + PORT);

                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Handle the client request (e.g., in a separate thread or method)
                handleClientRequest(clientSocket);

                // Close the client socket
                clientSocket.close();
                System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to handle client request
    private void handleClientRequest(Socket clientSocket) {
        try {
            // Create input and output streams for communication
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Receive data from the client
            String clientMessage = in.readLine();
            System.out.println("Received from client: " + clientMessage);
            listener.onUpdate(clientMessage);

            // Process the data (e.g., send a response)
            String response = "Server: Hello, Client!";
            out.println(LocalTime.now() + ": " + response);

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
