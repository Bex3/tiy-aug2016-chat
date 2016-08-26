package com.tiy.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dbashizi on 8/25/16.
 */
public class SampleServer implements Runnable {

    public static final String SERVER_TRANSACTION_OK = "TX::OK";
    public static final String SERVER_TRANSACTION_HISTORY_END = "TX::HISTORY::END";
    public static final String SERVER_COMMAND_HISTORY = "history";
    public static final String SERVER_COMMAND_NAME = "name=";
    public static final int PORT_NUMBER = 8005;

    private boolean stopServerFlag = false;

    private List<ClientHandle> clientHandles = new ArrayList<ClientHandle>();

    public void shutdownServer() {
        stopServerFlag = true;
    }

    public void run() {
        this.startServer();
    }

    private void startServer() {
        try {
            System.out.println("Starting server ...");

            // start a server on a specific port
            ServerSocket serverListener = new ServerSocket(PORT_NUMBER);

            while (!stopServerFlag) {
                System.out.println("Listener ready to accept connections ...");
                Socket incomingConnection = serverListener.accept();

                ConnectionHandler handler = new ConnectionHandler(incomingConnection, this);
                Thread handlerThread = new Thread(handler);
                handlerThread.start();
                // handleIncomingConnection(incomingConnection);
            }

            System.out.println("Shutting down server ...");

        } catch (IOException exception) {
            exception.printStackTrace();
        }

    }

    public void addClientToNotificationList(ClientHandle clientHandle) {
        clientHandles.add(clientHandle);
    }

    // ToDo something needs to be synchronized with multiple ConnectionHandler threads
    // broacasting messages at the same time
    public void broadcastMessage(String message) {
        for (ClientHandle clientHandle : clientHandles) {
            try {
                clientHandle.getWritingToClient().println(message);
                clientHandle.getReadingFromClient().readLine();
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }
    }

}
