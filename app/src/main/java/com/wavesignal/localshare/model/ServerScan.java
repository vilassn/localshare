package com.wavesignal.localshare.model;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ServerScan {
    public String getServerIP() {
        // private network space = > 192.168.0.0 - 192.168.255.255
        String network = "192.168."; // Replace with your LAN subnet
        int port = Server.PORT; // Replace with the port you want to scan
        List<String> hostAddresses = getHostAddresses();

        String subnet = "192.168.1"; // Default LAN subnet
        for (String address : hostAddresses) {
            if (address.startsWith(network)) {
                String[] parts = address.split("\\.");
                if (parts.length >= 3) {
                    subnet = parts[0] + "." + parts[1] + "." + parts[2];

                    System.out.println("Subnet: " + subnet);
                } else {
                    System.out.println("Invalid IP address format");
                }
                break;
            }
        }

        AtomicReference<String> finalHost = new AtomicReference<>();

        int nThreads = 256; // to search for 256 sub ip addresses
        // Discover server using multiple threads for different ip address
        List<Thread> workers = new ArrayList<>();
        for (int iw = 0; iw < nThreads; iw++) {
            final int ith = iw;  // Capture iw in a final variable for use in the lambda
            final String finalSubnet = subnet;
            Thread thread = new Thread(() -> {
                // Inside the thread, ith will have the same value as iw (first value is 0)
               System.out.println("Thread " + ith + " started.");

                String host = finalSubnet + "." + ith;
                //System.out.println("Scanning " + host);
                try {
                    InetSocketAddress socketAddress = new InetSocketAddress(host, port);
                    Socket socket = new Socket();
                    socket.connect(socketAddress, 1000); // Adjust the timeout as needed

                    // If the connection was successful, the port is open
                    System.out.println("Host: " + host + " Port " + port + " is open");
                    socket.close();

                    finalHost.set(host);
                } catch (Exception e) {
                    // e.printStackTrace();
                    // Port is closed or host is not reachable
                }
            });

            workers.add(thread);
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return finalHost.get();

//        for (int j = 0; j < 256; j++) {
//            String host = subnet + "." + j;
//            //System.out.println("Scanning " + host);
//            try {
//                InetSocketAddress socketAddress = new InetSocketAddress(host, port);
//                Socket socket = new Socket();
//                socket.connect(socketAddress, 100); // Adjust the timeout as needed
//
//                // If the connection was successful, the port is open
//                System.out.println("Host: " + host + " Port " + port + " is open");
//                socket.close();
//
//                return host;
//            } catch (Exception e) {
//                // e.printStackTrace();
//                // Port is closed or host is not reachable
//            }
//        }

        //return null;
    }

    public List<String> getHostAddresses() {
        List<String> inetAddressList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress.isLoopbackAddress()) {
                        continue; // Skip loopback addresses
                    }

                    if (inetAddress.getAddress().length == 4) {
                        System.out.println("IPv4 Address: " + inetAddress.getHostAddress());
                        inetAddressList.add(inetAddress.getHostAddress());
                    } else if (inetAddress.getAddress().length == 16) {
                        System.out.println("IPv6 Address: " + inetAddress.getHostAddress());
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return inetAddressList;
    }
}
