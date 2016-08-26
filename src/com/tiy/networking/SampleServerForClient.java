package com.tiy.networking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by dbashizi on 8/26/16.
 */
public class SampleServerForClient implements Runnable {

    public void run() {
        try {
            System.out.println("Client waiting for messages from the server ...");
            ServerSocket clientServerSocket = new ServerSocket(8010);

            // intentionally only listen to one connection at a time
            // because we only expect one server to connect at a given time
            Socket serverConnection = clientServerSocket.accept();
            System.out.println("Accepted a connection from " + serverConnection);

            // this is how we read from the client
            BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(serverConnection.getInputStream()));
            // this is how we write back to the client
            PrintWriter outputToServer = new PrintWriter(serverConnection.getOutputStream(), true);

            String serverMessage = inputFromServer.readLine();
            while (serverMessage != null) {
                System.out.println(serverMessage);
                outputToServer.println("Thanks");
                serverMessage = inputFromServer.readLine();
            }

            // ToDo: move this to finally block
            inputFromServer.close();
            outputToServer.close();
            serverConnection.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
