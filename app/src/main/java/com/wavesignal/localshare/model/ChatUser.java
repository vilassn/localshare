package com.wavesignal.localshare.model;

import java.util.Scanner;

public class ChatUser {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        ChatUser chatUser = new ChatUser();
        UpdateListener listener = new UpdateListener() {
            @Override
            public void onUpdate(String message) {
                System.out.println(message);
            }

            @Override
            public void onMessage(String message) {

            }
        };

        while (true) {
            System.out.println("Select an operation to perform:");
            System.out.println("1. Start Server");
            System.out.println("2. Start Client");
            System.out.println("3. Start Scanning");
            System.out.println("4. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    chatUser.startServer(listener);
                    break;
                case 2:
                    chatUser.startClient(listener);
                    break;
                case 3:
                    chatUser.startScanning();
                    break;
                case 4:
                    System.out.println("Exiting the program.");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    public void startServer(UpdateListener listener) {
        Thread thread = new Thread(() -> {
            Server server = new Server(listener);
            server.start();
        });
        thread.start();
    }

    public void startClient(UpdateListener listener) {
        Thread thread = new Thread(() -> {
            ServerScan serverScan = new ServerScan();
            String serverIP = serverScan.getServerIP();
            if (serverIP != null) {
                Client client = new Client(serverIP, listener);
                client.start();
            } else {
                System.out.println("Server is not available...!");
            }
        });
        thread.start();
    }

    public void startScanning() {
        Thread thread = new Thread(() -> {
            ServerScan serverScan = new ServerScan();
            serverScan.getServerIP();
        });
        thread.start();
    }
}
