package com.tiy.networking;

import org.junit.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Created by dbashizi on 8/26/16.
 */
public class ConnectionHandlerTest {

    static SampleServer testChatServer;
    final static int TARGET_PORT_NUMBER = SampleServer.PORT_NUMBER;
    final static String HOST_ADDRESS = "localhost";

    @BeforeClass
    public static void setUp() throws Exception {
        // instantiate and start the server, so that each test method can simply send
        // messages
        testChatServer = new SampleServer();
        new Thread(testChatServer).start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // shut down the server after we're done testing specific types of messages
        shutdownServer();
    }

    @Test
    public void testSingleMessageToServer() throws IOException {
        Socket clientSocket = new Socket(HOST_ADDRESS, TARGET_PORT_NUMBER);

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        out.println(SampleServer.SERVER_COMMAND_NAME + "Tester");

        String serverResponse = in.readLine();
        assertEquals(SampleServer.SERVER_TRANSACTION_OK, serverResponse);
    }

    @Test
    public void testMessageHistory() throws IOException {
        Socket clientSocket = new Socket(HOST_ADDRESS, TARGET_PORT_NUMBER);

        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        out.println(SampleServer.SERVER_COMMAND_NAME + "Tester");
        String serverResponse = in.readLine();
        assertEquals(SampleServer.SERVER_TRANSACTION_OK, serverResponse);

        out.println("Testing from my unit test suite ...");
        serverResponse = in.readLine();
        System.out.println(serverResponse);
        out.println("And sending another line of text for good measure ...");
        System.out.println(in.readLine());

        // now test sending the history command and reading the results back
        out.println(SampleServer.SERVER_COMMAND_HISTORY);
        int numMessagesInHistory = 0;
        serverResponse = in.readLine();
        while (serverResponse != null &&
               !serverResponse.equalsIgnoreCase(SampleServer.SERVER_TRANSACTION_HISTORY_END)) {
            numMessagesInHistory++;
            System.out.println("History response: " + serverResponse);
            serverResponse = in.readLine();
        }
        assertEquals(3, numMessagesInHistory);
    }

    /**
     * Utility method to shut down the server
     */
    private static void shutdownServer() {
        testChatServer.shutdownServer();
        // establish a connection to trigger the actual server shutdown
        try {
            Socket clientSocket = new Socket(HOST_ADDRESS, TARGET_PORT_NUMBER);

            // once we connect to the server, we also have an input and output stream
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // send the server an arbitrary message
            out.println("name=Tester");
        } catch (Exception exception) {
            System.out.println("Expected exception because server is already shut down: " + exception.getMessage());
        }

    }
}