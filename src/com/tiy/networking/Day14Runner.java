package com.tiy.networking;

/**
 * Created by dbashizi on 8/25/16.
 */
public class Day14Runner {
    public static void main(String[] args) {
        System.out.println("Running ... ");

        SampleServer myServer = new SampleServer();
        new Thread(myServer).start();
    }
}
