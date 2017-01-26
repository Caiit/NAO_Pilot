package nl.mprog.nao_pilot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
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
    private View view;
    private Handler handler;
    private Socket client;
    private boolean shutdown = false;
    private DataInputStream in;
    private DataOutputStream out;
    private String buffer;
    private Queue outMessages = new LinkedList();
    private Queue inMessages = new LinkedList();
    private Queue images = new LinkedList();

    public static NetworkThread getInstance() {
        if (thread == null) thread = new NetworkThread();
        return thread;
    }

    private NetworkThread() {
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public boolean getShutdown() {
        return shutdown;
    }

    public void run() {
        createSocket();
        while (!shutdown) {
            handleMessage((JSONObject) outMessages.poll());
            receiveMessages();
        }
        // Close client
        try {
            if (out != null) {
                out.writeUTF(toJson("disconnect", "Closing connection from the app.").toString());
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
        try {
            Log.d("Connecting to " + IP + " on port " + port, "createSocket: ");
            client = new Socket();
            client.connect(new InetSocketAddress(IP, port), 1000);
            Log.d(String.valueOf(client), "createSocket: socket created");
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());

            Log.d("Just connected to " + client.getRemoteSocketAddress(), "createSocket: ");

            // Send message to tell connection is created
//            out.writeUTF(toJson("connect", "Hello robot from the app.").toString());
//
//            // Wait until message from server available
//            while (in.available() == 0) {
//            }
//
//            byte[] bytes = new byte[in.available()];
//            in.read(bytes);
//            Log.d("Server says: " + fromBytes(bytes), "createSocket: ");
        } catch (IOException e) {
            Log.d("Could not connect", "createSocket: No connection possible");
            e.printStackTrace();
            shutdown = true;
        }
    }

    private String fromBytes( byte[] bytes) {
        for (int i = bytes.length-1; i > 0 ; --i)
        {
            if (bytes[i] == 0) {
                return new String(bytes, 0, i);
            }
        }
        return new String(bytes);
    }

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
                    Log.d("No number", "receiveMessages: Message doesn't start with a number");
                    return;
                }
//                Log.d(String.valueOf(size), "receiveMessages: size");
                // Read message
                String message = "";
                String error = "";
                while (in.available() < size )
                {
//                    System.out.println("bytes avail: " + in.available());
                }
                while (message.length() < size - 1 ) {
                    int bufferSize = size - message.length();

                    byte[] byteMessage = new byte[bufferSize];
                    int bytesAvailable = in.available();
                    int bytesRead = in.read(byteMessage);
//                    if (bufferSize > 0 ) {
//                        Log.d(size + ", " + bytesAvailable + ", " + bytesRead + ", " + message.length(), "receiveMessages: buffersize");
//                    }

                    byte[] validBytes = Arrays.copyOfRange(byteMessage, 0, bytesRead);
                    message += fromBytes(validBytes);
                }
//                Log.d(String.valueOf(message.length()), "receiveMessages: string length");
//                System.out.println("<" + message.substring(Math.max(0, size - 500)) + ">");
                // Add message to the messages handler of the main thread
                Message msg = Message.obtain();
                msg.obj = message;
                handler.sendMessage(msg);
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
        outMessages.add(message);
    }

    void closeThread() {
        shutdown = true;
    }
}
