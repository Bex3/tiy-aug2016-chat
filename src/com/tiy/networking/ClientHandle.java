package com.tiy.networking;

import java.io.BufferedReader;
import java.io.PrintWriter;

/**
 * Created by dbashizi on 8/26/16.
 */
public class ClientHandle {
    private BufferedReader readingFromClient;
    private PrintWriter writingToClient;

    public ClientHandle(BufferedReader readingFromClient, PrintWriter writingToClient) {
        this.readingFromClient = readingFromClient;
        this.writingToClient = writingToClient;
    }

    public BufferedReader getReadingFromClient() {
        return readingFromClient;
    }

    public void setReadingFromClient(BufferedReader readingFromClient) {
        this.readingFromClient = readingFromClient;
    }

    public PrintWriter getWritingToClient() {
        return writingToClient;
    }

    public void setWritingToClient(PrintWriter writingToClient) {
        this.writingToClient = writingToClient;
    }
}
