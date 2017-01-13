package nl.mprog.nao_pilot;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The Robot class handles the connection with a NAO robot.
 */

public class Robot {

    String IP;
    /**
     * Construct a robot object by setting the IP of the robot.
     */
    Robot(String IP) {
        this.IP = IP;
        createClient();
    }

    private void createClient() {
        int port = 3006;
        try {
            Log.d("servername: " + IP + " port: " + port, "createClient: serverName");
            Socket client = new Socket(IP, port);
            Log.d("Just connected to: " + String.valueOf(client.getRemoteSocketAddress()), "createClient: servername");

            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            DataInputStream in = new DataInputStream(client.getInputStream());
            out.writeUTF("Hello robot");

            while (in.available() == 0) {
                // TODO: hier wat moois inmaken
            }

            byte[] bytes = new byte[in.available()];
            in.read(bytes);
            System.out.println("Server says: " + new String(bytes));
            client.close();
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

}
