import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by dbashizi on 9/7/16.
 */
public class ClientBaseline {

    final String HOST_ADDRESS = "localhost";
    final int PORT_NUMBER = 8005;

    public static void main(String[] args) {
        new ClientBaseline().runClient();
    }

    public void runClient() {
        try {
            System.out.println("Connecting to " + HOST_ADDRESS + " on port " + PORT_NUMBER + " ...");
            Socket clientConnection = new Socket(HOST_ADDRESS, PORT_NUMBER);

            System.out.println("Connected!");

            // once we connect to the server, we also have an input and output stream
            PrintWriter outToServer = new PrintWriter(clientConnection.getOutputStream(), true);
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientConnection.getInputStream()));
            System.out.println("Input and Output Streams initialized");

            // we could replace this with a name we get from the user
            outToServer.println("name=" + "client-baseline");
            // make sure we capture the response coming back from the server
            String serverResponse = inFromServer.readLine();
            System.out.println("Server says: " + serverResponse);

            // now get into a loop to:
            // 1. ask for user input
            // 2. send user input to the server
            // 3. read the response from the server
            // 4. print the response out to screen
            Scanner inputScanner = new Scanner(System.in);
            while (true) {
                System.out.println("Enter your message to send: " );
                String userMessage = inputScanner.nextLine();

                if (userMessage.equalsIgnoreCase("exit")) {
                    break;
                }

                outToServer.println(userMessage);
                serverResponse = inFromServer.readLine();
                System.out.println("Server says: " + serverResponse);
            }

            // these should be in a finally block, but keeping here for simplicity
            inFromServer.close();
            outToServer.close();
            clientConnection.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
