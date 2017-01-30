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

public class WalkFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    View view;
    NetworkThread networkThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_walk, container, false);
        networkThread = NetworkThread.getInstance();

        // Set listeners
        ((SeekBar) view.findViewById(R.id.seekBarSpeedWalk)).setOnSeekBarChangeListener(this);
        view.findViewById(R.id.upButton).setOnTouchListener(this);
        view.findViewById(R.id.downButton).setOnTouchListener(this);
        view.findViewById(R.id.leftButton).setOnTouchListener(this);
        view.findViewById(R.id.rightButton).setOnTouchListener(this);
        view.findViewById(R.id.leftTurnButton).setOnTouchListener(this);
        view.findViewById(R.id.rightTurnButton).setOnTouchListener(this);
        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        TextView speedText = (TextView) view.findViewById(R.id.speedTextWalk);
        speedText.setText("Speed: " + String.valueOf(i) + " %");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent motionEvent) {
        String speedText = (String) ((TextView) view.findViewById(R.id.speedTextWalk)).getText();
        String speed = speedText.split("\\s+")[1];
        float xSpeed = 0;
        float ySpeed = 0;
        float thetaSpeed = 0;
        switch (v.getId()) {
            case R.id.upButton:
                xSpeed = Float.parseFloat(speed) / 100;
                break;
            case R.id.downButton:
                xSpeed = - Float.parseFloat(speed) / 100;
                break;
            case R.id.leftButton:
                ySpeed = Float.parseFloat(speed) / 100;
                break;
            case R.id.rightButton:
                ySpeed = - Float.parseFloat(speed) / 100;
                break;
            case R.id.leftTurnButton:
                thetaSpeed = Float.parseFloat(speed) / 100;
                break;
            case R.id.rightTurnButton:
                thetaSpeed = - Float.parseFloat(speed) / 100;
                break;
            default:
                break;
        }

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            // Walk with speed
            if (networkThread != null) {
                JSONObject json = new JSONObject();
                try {
                    json.put("type", "walk");
                    json.put("x_speed", xSpeed);
                    json.put("y_speed", ySpeed);
                    json.put("theta_speed", thetaSpeed);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                networkThread.sendMessage(json);
            }
            return true;
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            // Stop walking
            JSONObject json = new JSONObject();
            try {
                json.put("type", "walk");
                json.put("x_speed", 0);
                json.put("y_speed", 0);
                json.put("theta_speed", 0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            networkThread.sendMessage(json);
            return true;
        }
        return false;
    }
}
