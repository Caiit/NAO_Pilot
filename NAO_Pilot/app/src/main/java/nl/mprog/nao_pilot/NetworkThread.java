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
            out.writeUTF(toJson("connect", "Hello robot from the app.").toString());

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
                // Read message
                byte[] bytes = new byte[in.available()];
                in.read(bytes);
                String message = new String(bytes);
                // Add message to buffer, if no end sign in message, return
                buffer += message;
                Log.d(String.valueOf(buffer.length()), "receiveMessages: buffer");
                // Split message on end sign, begin is complete message, rest is new buffer
                final String[] parts = buffer.split("end \0");
                Log.d(String.valueOf(parts.length), "receiveMessages: parts");
                if (parts.length == 1 && !buffer.endsWith("end \0")) { return; }
                if (parts.length > 1) {
                    buffer = parts[1];
                } else {
                    buffer = "";
                }
                Log.d(buffer, "receiveMessages: test");
                // Add image messages to images queue
                if (parts[0].charAt(0) != '{') {
                    Log.d(String.valueOf(parts[0].length()), "receiveMessages: len");
                    images.add(StringToBitMap(parts[0]));
                    Message msg = Message.obtain();
                    msg.obj = parts[0];
                    handler.sendMessage(msg);
                }
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

    public JSONObject getInMessage() {
        Log.d(String.valueOf(inMessages.size()), "getInMessage: ");
        return (JSONObject) inMessages.poll();
    }

    public Bitmap getImage() {
        return (Bitmap) images.poll();
    }

    void closeThread() {
        shutdown = true;
    }

    private Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}
