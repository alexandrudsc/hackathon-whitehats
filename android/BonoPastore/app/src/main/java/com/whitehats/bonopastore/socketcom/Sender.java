package com.whitehats.bonopastore.socketcom;

import android.util.Log;
import com.whitehats.bonopastore.User;

import java.io.*;
import java.net.UnknownHostException;

public class Sender {

    private static String TAG = "SocketThread";

    public static void sendLocationMessage(User user)
    {
        StringBuilder msg = new StringBuilder();
        msg.append(user.getSimNumber());
        msg.append(" L ");
        msg.append(user.getLastLocation().component1() + " ");
        msg.append(user.getLastLocation().component2() + " ");

        Log.i(TAG, msg.toString());

        sendMessage(msg.toString());

    }

    static void sendMessage(String message) {
        Client client = new Client();
        client.hostname = ServerConfig.hostname;
        client.port = ServerConfig.port;
        Thread thr = new Thread(new ClientThread(client, message));
        thr.start();

    }

    static class ClientThread implements Runnable {
        private Client c;
        private String message;
        ClientThread(Client c, String message) {
            super();
            this.c = c;
            this.message = message;
        }

        @Override
        public void run() {

            try {
                for (int i = 0; i <= 3; i++)
                {
                    this.c.send(message);
                }
            } catch (UnknownHostException e) {
                Log.e(TAG, "Don't know about host: " + this.c.hostname);
            } catch (IOException e) {
                Log.e(TAG, "Couldn't get I/O for the connection to: " + this.c.hostname);
                e.printStackTrace();
            }
        }
    }
}
