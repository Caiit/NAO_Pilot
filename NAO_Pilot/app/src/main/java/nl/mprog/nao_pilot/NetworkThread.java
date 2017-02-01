package nl.mprog.nao_pilot;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * Singleton NetworkThread creates a client that connects to the server on the robot.
 * Sends messages from the app to the robot.
 * Receives messages from the robot and add them to the handler of the mainActivity.
 */

class NetworkThread implements Runnable {

    private static NetworkThread thread = new NetworkThread();
    private String IP;
    private Handler handler;
    private Socket client;
    private boolean shutdown = false;
    private DataInputStream in;
    private DataOutputStream out;
    private Queue outMessages = new LinkedList();

    /**
     * Get the network thread singleton.
     */
    static NetworkThread getInstance() {
        if (thread == null) thread = new NetworkThread();
        return thread;
    }

    /**
     * NetworkThread constructor.
     */
    private NetworkThread() {
    }

    /**
     * Run the networkthread. Create a socket connection and send and
     * receive messages until connection is closed.
     */
    public void run() {
        createSocket();
        while (!shutdown) {
            sendMessage((JSONObject) outMessages.poll());
            receiveMessages();
        }
        // Close client
        try {
            if (out != null) {
                String msg = toJson("disconnect", "Closing connection from the app.").toString();
                outMessages.add(msg);
                Log.d("Closed successfully", "run: Connection closed");
            }
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        IP = null;
        thread = null;
    }

    /**
     * Return if robot is connected or not.
     */
    boolean connected() {
        return IP != null;
    }

    /**
     * Set the ip of the robot.
     */
    void setIP(String IP) {
        this.IP = IP;
    }

    /**
     * Set the handler of the mainactivity.
     */
    void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * Add a message to the outMessage queue.
     */
    void addToSend(JSONObject message) {
        outMessages.add(message);
    }

    /**
     * Close the thread.
     */
    void closeThread() {
        shutdown = true;
    }

    /**
     * Create a socket client that connects to the robot.
     */
    private void createSocket() {
        int port = 3006;
        try {
            System.out.println("Connecting to " + IP + " on port " + port);
            client = new Socket();
            client.connect(new InetSocketAddress(IP, port), 1000);
            // Create input and output streams
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());
            System.out.println("Just connected to " + client.getRemoteSocketAddress());
        } catch (IOException e) {
            // Connection failed, send error message
            Message msg = Message.obtain();
            msg.obj = "{\"type\": \"disconnect\", \"text\": \"Could not connect\"}";
            handler.sendMessage(msg);
            e.printStackTrace();
            shutdown = true;
        }
    }

    /**
     * Receive messages from the robot via the socket connection.
     */
    private void receiveMessages() {
        try {
            if (in.available() > 0) {
                // Get size of messsage
                byte[] byteSize = new byte[8];
                in.read(byteSize);
                int size;
                try {
                    size = Integer.parseInt(new String(byteSize));
                } catch (NumberFormatException nfe) {
                    System.out.println("Message doesn't start with a number");
                    return;
                }
                // Read message
                String message = "";
                while (in.available() < size ) {}
                while (message.length() < size - 1) {
                    int bufferSize = size - message.length();
                    byte[] byteMessage = new byte[bufferSize];
                    int bytesRead = in.read(byteMessage);
                    byte[] validBytes = Arrays.copyOfRange(byteMessage, 0, bytesRead);
                    message += fromBytes(validBytes);
                }
                // Add message to the messages handler of the main thread
                Message msg = Message.obtain();
                msg.obj = message;
                handler.sendMessage(msg);
            }
        } catch (IOException e) {
            System.out.println("Receiving message failed.");
            e.printStackTrace();
        }
    }

    /**
     * Send the messages from the outMessages queue to the robot.
     */
    private void sendMessage(JSONObject message) {
        try {
            if (out != null && message != null) {
                String strMessage = message.toString();
                String len = String.format("%08d", strMessage.length());
                out.writeUTF(len + strMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Sending message failed.");
        }
    }

    /**
     * Convert byte array to string.
     */
    private String fromBytes(byte[] bytes) {
        for (int i = bytes.length - 1; i > 0; i--) {
            if (bytes[i] == 0) {
                return new String(bytes, 0, i);
            }
        }
        return new String(bytes);
    }

    /**
     * Convert the given message of to JSON.
     */
    private JSONObject toJson(String type, String message) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("text", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
