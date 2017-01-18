package nl.mprog.nao_pilot;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The walk fragment of the app.
 * Let the robot walk.
 */

public class WalkFragment extends Fragment {

    View view;
    NetworkThread networkThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_walk, container, false);
        networkThread = NetworkThread.getInstance();
        handleButtons();
        return view;
    }

    private void handleButtons() {
        ImageButton up = (ImageButton) view.findViewById(R.id.upButton);
        up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Walk with speed
                    if (networkThread != null) {
                        // TODO: speed toevoegen
//                        String volumeText = (String) ((TextView) findViewById(R.id.volumeText)).getText();
//                        String volume = volumeText.split("\\s+")[1];
//                        String speedText = (String) ((TextView) findViewById(R.id.speedText)).getText();
//                        String speed = speedText.split("\\s+")[1];
//                        String pitchText = (String) ((TextView) findViewById(R.id.pitchText)).getText();
//                        String pitch = pitchText.split("\\s+")[1]
                        Log.d("walking", "onTouch: touching");
                        float xSpeed = 1;
                        JSONObject json = new JSONObject();
                        try {
                            json.put("type", "walk");
                            json.put("x_speed", xSpeed);
                            json.put("y_speed", 0);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        networkThread.sendMessage(json);
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Stop walking
                    Log.d("stop walking", "onTouch: stop touching");
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "walk");
                        json.put("x_speed", 0);
                        json.put("y_speed", 0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    networkThread.sendMessage(json);
                    return true;
                }
                return false;
            }
        });
    }

}
