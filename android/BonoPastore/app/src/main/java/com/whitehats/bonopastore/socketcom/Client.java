package com.whitehats.bonopastore.socketcom;

import android.util.Log;

import java.io.*;
import java.net.*;

public class Client {

    public String hostname;
    public int port;

    private static String TAG = Client.class.getName();

    public void send(String message) throws IOException {

        // declaration section:
        // clientSocket: our client socket
        // os: output stream
        // is: input stream

        Socket clientSocket = null;
        DataOutputStream os = null;
        BufferedReader is = null;

        // Initialization section:
        // Try to open a socket on the given port
        // Try to open input and output streams
        clientSocket = new Socket(hostname, port);
        os = new DataOutputStream(clientSocket.getOutputStream());
        is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


        // If everything has been initialized then we want to write some data
        // to the socket we have opened a connection to on the given port
        if (clientSocket == null || os == null || is == null) {
            System.err.println("Something is wrong. One variable is null.");
            return;
        }
        Log.d(TAG, "Client send" + message);
        clientSocket.getOutputStream().write(message.getBytes());
        clientSocket.getOutputStream().flush();
        // clean up:
        // close the output stream
        // close the input stream
        // close the socket

        os.close();
        is.close();
        clientSocket.close();

    }
}
