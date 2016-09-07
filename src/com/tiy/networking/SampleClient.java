package com.tiy.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by dbashizi on 8/25/16.
 */
public class SampleClient {

    static final String HOST_ADDRESS = "localhost";
//    static final String HOST_ADDRESS = "10.0.0.129";
    static final int PORT_NUMBER = 8005;
//    static final int PORT_NUMBER = 8080;

    public static void main(String[] args) {
        new SampleClient().startClient();
    }

    public void startClient() {
        System.out.println("Running Client ...");

        startClientServer();

        try {
            Scanner inputScanner = new Scanner(System.in);
            // connect to the server on the target port
            Socket clientSocket = new Socket(HOST_ADDRESS, PORT_NUMBER);

            // once we connect to the server, we also have an input and output stream
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            System.out.println("What's your name?");
            String name = inputScanner.nextLine();

            // send the server an arbitrary message
            out.println("name=" + name);
            // read what the server returns
            String serverResponse = in.readLine();
            while (serverResponse != null) {
                System.out.println("Enter message:");
                String messageToServer = inputScanner.nextLine();
                System.out.println("Sending " + messageToServer + " to the server ...");
                // handle message history from the server
                if (messageToServer.equalsIgnoreCase("history")) {
                    out.println(messageToServer);
                    serverResponse = in.readLine();
                    System.out.println("** Message History **");
                    while (serverResponse != null && !serverResponse.equalsIgnoreCase(SampleServer.SERVER_TRANSACTION_HISTORY_END)) {
                        System.out.println(serverResponse);
                        serverResponse = in.readLine();
                    }
                    System.out.println("** End Message History **");
                } else {
                    out.println(messageToServer);
                    serverResponse = in.readLine();
                    if (serverResponse != null && !serverResponse.equalsIgnoreCase(SampleServer.SERVER_TRANSACTION_OK)) {
                        System.out.println("Server says: " + serverResponse);
                    } else {
                        System.out.println("Server ended transmission");
                    }
                }
            }

            // close the connection
            clientSocket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void startClientServer() {
        System.out.println("startClientServer() ... ");
        SampleServerForClient clientServer = new SampleServerForClient();
        new Thread(clientServer).start();
    }

}
