package nl.mprog.nao_pilot;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * NetworkThread creates a client that connects to the server on the robot.
 */

public class NetworkThread implements Runnable {

    private String IP;
    private Socket client;
    private boolean shutdown = false;
    private DataInputStream in;
    private DataOutputStream out;
    private Queue messageQueue = new LinkedList();

    NetworkThread(String IP) {
        this.IP = IP;
    }

    public void run() {
        createSocket();
        while (!shutdown) {
            receiveMessages();
            handleMessage((String) messageQueue.poll());
        }
        // Close client
        try {
            if (out != null) {
                out.writeUTF("Closing connection from the app.");
                Log.d("Closed successfully", "run: Connection closed");
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSocket() {
        int port = 3006;
        Log.d(String.valueOf(client), "createSocket: before");
        try {
            Log.d("Connecting to " + IP + " on port " + port, "createSocket: ");
            Log.d("test", "createSocket: creating socket");
            client = new Socket();
            client.connect(new InetSocketAddress(IP, port), 1000);
            Log.d(String.valueOf(client), "createSocket: socket created");
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());
            Log.d(String.valueOf(client.isConnected()), "createSocket: ");

            Log.d("Just connected to " + client.getRemoteSocketAddress(), "createSocket: ");

            // Send message to tell connection is created
            out.writeUTF("Hello robot from the app.");

            // Wait until message from server available
            while (in.available() == 0) {
            }

            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            Log.d("Server says: " + new String(bytes), "createSocket: ");
        } catch(SocketTimeoutException e) {
            Log.d("Could not connect", "createSocket: No connection possible");
            e.printStackTrace();
            shutdown = true;
        } catch (ConnectException e) {
            Log.d("Could not connect", "createSocket: No connection possible");
            e.printStackTrace();
            shutdown = true;
        } catch (IOException e) {
            Log.d("Could not connect", "createSocket: No connection possible");
            e.printStackTrace();
            shutdown = true;
        }
    }

    private void receiveMessages() {
        try {
            if (in.available() > 0) {
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                Log.d("Server says: " + new String(bytes), "receiveMessages: ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(String message) {
        try {
            if (out != null && message != null) {
                out.writeUTF(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Send failed", "sendMessage: sending message failed.");
        }
    }

    void sendMessage(String message) {
        messageQueue.add(message);
    }

    void closeThread() {
        shutdown = true;
    }
}
