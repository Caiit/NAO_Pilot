package nl.mprog.nao_pilot;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
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

    private static NetworkThread thread = new NetworkThread();
    private String IP;
    private Socket client;
    private boolean shutdown = false;
    private DataInputStream in;
    private DataOutputStream out;
    private Queue messageQueue = new LinkedList();

    public static NetworkThread getInstance() {
        if (thread == null) thread = new NetworkThread();
        return thread;
    }

    private NetworkThread() {
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void run() {
        createSocket();
        while (!shutdown) {
            handleMessage((JSONObject) messageQueue.poll());
            receiveMessages();
        }
        // Close client
        try {
            if (out != null) {
                out.writeUTF(toJson("disconnect", "Closing connection from the app."));
                Log.d("Closed successfully", "run: Connection closed");
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        thread = null;
    }

    private void createSocket() {
        int port = 3006;
        Log.d(String.valueOf(client), "createSocket: before");
        try {
            Log.d("Connecting to " + IP + " on port " + port, "createSocket: ");
            client = new Socket();
            client.connect(new InetSocketAddress(IP, port), 1000);
            Log.d(String.valueOf(client), "createSocket: socket created");
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());

            Log.d("Just connected to " + client.getRemoteSocketAddress(), "createSocket: ");

            // Send message to tell connection is created
            out.writeUTF(toJson("connect", "Hello robot from the app."));

            // Wait until message from server available
            while (in.available() == 0) {
            }

            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            Log.d("Server says: " + new String(bytes), "createSocket: ");
        } catch (IOException e) {
            Log.d("Could not connect", "createSocket: No connection possible");
            e.printStackTrace();
            shutdown = true;
        }
    }

    private String toJson(String type, String message) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("text", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    private void receiveMessages() {
        try {
            if (in.available() > 0) {
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                Log.d("Server says: " + new String(bytes), "receiveMessages: ");
            }
        } catch (IOException e) {
            Log.d("Receiving failed", "receiveMessages: ");
            e.printStackTrace();
        }
    }

    private void handleMessage(JSONObject message) {
        try {
            if (out != null && message != null) {
                out.writeUTF(message.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Send failed", "sendMessage: sending message failed.");
        }
    }

    void sendMessage(JSONObject message) {
        messageQueue.add(message);
    }

    void closeThread() {
        shutdown = true;
    }
}
