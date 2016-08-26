package com.tiy.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by dbashizi on 8/25/16.
 */
public class ConnectionHandler implements Runnable {

    Socket connection;
    SampleServer chatServer;

    public ConnectionHandler(Socket incomingConnection, SampleServer chatServer) {
        this.connection = incomingConnection;
        this.chatServer = chatServer;
    }

    public void run() {
        try {
            handleIncomingConnection(connection);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
    }

    private void handleIncomingConnection(Socket clientSocket) throws IOException {

        BufferedReader inputFromClient = null;
        PrintWriter outputToClient = null;
        try {
            System.out.println("Connection accepted");

            System.out.println("clientSocket = " + clientSocket);

            // display information about who just connected to our server
            System.out.println("Incoming connection from " + clientSocket.getInetAddress().getHostAddress());

            // this is how we read from the client
            inputFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // this is how we write back to the client
            outputToClient = new PrintWriter(clientSocket.getOutputStream(), true);

            // genericConnectionHandling(inputFromClient, outputToClient, clientSocket);

            handleClientConversation(inputFromClient, outputToClient, clientSocket);
        } finally {
            inputFromClient.close();
            outputToClient.close();
        }
    }

    private void handleClientConversation(BufferedReader inputFromClient,
                                          PrintWriter outputToClient,
                                          Socket clientSocket) throws IOException {
        String clientName;
        List<String> messageHistory = new ArrayList<String>();
        // we expect the client to send their name as the first message
        // in this format: name=name-of-client
        String clientMessage = inputFromClient.readLine();

//        System.out.println("What did we get from the client? - here it is: " + clientMessage);
        if (clientMessage != null &&
            clientMessage.indexOf(SampleServer.SERVER_COMMAND_NAME) == 0) {
            clientName = clientMessage.substring(SampleServer.SERVER_COMMAND_NAME.length());
            System.out.println(clientName + " is connected!");
            outputToClient.println(SampleServer.SERVER_TRANSACTION_OK);

            connectAndSaveClientServerStreams(clientSocket);

            // start reading messages from the client as they come in
            // until the client says "exit"
            clientMessage = inputFromClient.readLine();
            while (clientMessage != null && !clientMessage.equalsIgnoreCase("exit")) {
                System.out.println(clientName + " says: " + clientMessage);

                chatServer.broadcastMessage(clientName + " says: " + clientMessage);

                messageHistory.add(clientMessage);
                if (clientMessage.equalsIgnoreCase(SampleServer.SERVER_COMMAND_HISTORY)) {
                    System.out.println("** sending message history (" +
                            messageHistory.size() + ") to " + clientName + " **");
                    for (String archivedMessage : messageHistory) {
                        outputToClient.println(archivedMessage);
                    }
                    outputToClient.println(SampleServer.SERVER_TRANSACTION_HISTORY_END);
                } else {
                    outputToClient.println(clientName + " says: " + clientMessage);
                }
                clientMessage = inputFromClient.readLine();
            }
            outputToClient.println("Ending transmission - thanks for the chat!");
        } else {
            System.out.println("Incorrect initial message!");
            outputToClient.println("Incorrect message - transmission should start with your name in this format: " +
                                    SampleServer.SERVER_COMMAND_NAME + "name-of-client");
        }
    }

    private void connectAndSaveClientServerStreams(Socket clientSocket) {
        System.out.println("connectAndSaveclientServerStreams()");
        try {
            System.out.println("Connection to " + clientSocket.getInetAddress().getHostAddress());
            Socket clientServerSocket = new Socket(clientSocket.getInetAddress().getHostAddress(), 8010);

            PrintWriter out = new PrintWriter(clientServerSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientServerSocket.getInputStream()));
            ClientHandle clientHandle = new ClientHandle(in, out);
            chatServer.addClientToNotificationList(clientHandle);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    private void genericConnectionHandling(BufferedReader inputFromClient,
                                           PrintWriter outputToClient,
                                           Socket clientSocket) throws IOException {
        // read from the input until the client disconnects
        String inputLine;
        while ((inputLine = inputFromClient.readLine()) != null) {
            System.out.println("Received message: " + inputLine + " from " + clientSocket.toString());
            outputToClient.println("Message received loud and clear");
        }

    }
}
