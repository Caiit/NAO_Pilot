package nl.mprog.nao_pilot;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The moves fragment of the app.
 * Let the robot perform the appropriate move.
 */

public class MovesFragment extends Fragment implements View.OnClickListener {

    View view;
    NetworkThread networkThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_moves, container, false);
        networkThread = NetworkThread.getInstance();

        // Set button listeners
        view.findViewById(R.id.standButton).setOnClickListener(this);
        view.findViewById(R.id.sitButton).setOnClickListener(this);
        view.findViewById(R.id.waveButton).setOnClickListener(this);
        view.findViewById(R.id.robotButton).setOnClickListener(this);
        view.findViewById(R.id.kickButton).setOnClickListener(this);
        view.findViewById(R.id.kissesButton).setOnClickListener(this);
        view.findViewById(R.id.bowButton).setOnClickListener(this);
        view.findViewById(R.id.wipeButton).setOnClickListener(this);

        return view;
    }


    /**
     * Handle the moves buttons.
     */
    @Override
    public void onClick(View view) {
        String fileName = "";
        switch (view.getId()) {
            case R.id.standButton:
                fileName = "stand";
                break;
            case R.id.sitButton:
                fileName = "sit";
                break;
            case R.id.waveButton:
                fileName = "wave.txt";
                break;
            case R.id.robotButton:
                fileName = "robot.txt";
                break;
            case R.id.kickButton:
                fileName = "kick.txt";
                break;
            case R.id.kissesButton:
                fileName = "blow_kisses.txt";
                break;
            case R.id.bowButton:
                fileName = "bow.txt";
                break;
            case R.id.wipeButton:
                fileName = "wipe_forehead.txt";
            default:
                break;
        }

        if (networkThread != null && !fileName.equals("")) {
            JSONObject json = new JSONObject();
            try {
                json.put("type", "moves");
                json.put("name", fileName);
//                if (fileName.equals("sit") || fileName.equals("stand")) {
//                    json.put("file", fileName);
//                } else {
//                    // Get file
//                    InputStream is = getContext().getAssets().open(fileName);
//                    int size = is.available();
//                    byte[] byteFile = new byte[size];
//                    is.read(byteFile);
//                    is.close();
//                    // Convert the file into a string.
//                    String file = new String(byteFile);
//                    json.put("file", file);
//                }
                networkThread.addToSend(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}