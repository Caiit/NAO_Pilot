package nl.mprog.nao_pilot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    NetworkThread networkThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void connectRobot(View view) {
        Button connectButton = (Button) view;
        if (connectButton.getText().equals("Connect")) {
            String IP = ((EditText) findViewById(R.id.IP)).getText().toString();

            // Start thread with robot connection
            networkThread = new NetworkThread(IP);
            new Thread(networkThread).start();
            connectButton.setText("Disconnect");
        } else {
            // Close thread
            if (networkThread != null) {
                networkThread.closeThread();
            }
            connectButton.setText("Connect");
        }
    }

    public void sendMessage(View view) {
        if (networkThread != null) {
            networkThread.sendMessage("Hello Robot!");
        }
    }
}
