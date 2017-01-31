package nl.mprog.nao_pilot;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The speak fragment of the app.
 * Let the robot say the appropriate text.
 */

public class SpeakFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private View view;
    private NetworkThread networkThread;
    private int volume = 100;
    private int speed = 100;
    private int pitch = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_speak, container, false);
        networkThread = NetworkThread.getInstance();
        Log.d("SPEAK FRAGMENT", "onCreateView: ON CREATE VIEW");

        // Set listeners
        ((SeekBar) view.findViewById(R.id.seekBarVolume)).setOnSeekBarChangeListener(this);
        ((SeekBar) view.findViewById(R.id.seekBarSpeed)).setOnSeekBarChangeListener(this);
        ((SeekBar) view.findViewById(R.id.seekBarPitch)).setOnSeekBarChangeListener(this);
        view.findViewById(R.id.sayButton).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (networkThread != null) {
            EditText messageET = (EditText) view.findViewById(R.id.sayText);
            String message = messageET.getText().toString();

            JSONObject json = new JSONObject();
            try {
                json.put("type", "speak");
                json.put("text", message);
                json.put("volume", volume);
                json.put("speed", speed);
                json.put("pitch", pitch);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            networkThread.sendMessage(json);
            // Hide keyboard after click
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(messageET.getWindowToken(), 0);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {
            case R.id.seekBarVolume:
                TextView volumeText = (TextView) getActivity().findViewById(R.id.volumeText);
                volumeText.setText("Volume: " + String.valueOf(i) + " %");
                volume = i;
                break;
            case R.id.seekBarSpeed:
                TextView speedText = (TextView) getActivity().findViewById(R.id.speedText);
                speedText.setText("Speed: " + String.valueOf(i + 50) + " %");
                speed = i;
                break;
            case R.id.seekBarPitch:
                TextView pitchText = (TextView) getActivity().findViewById(R.id.pitchText);
                pitchText.setText("Pitch: " + String.valueOf(i + 50) + " %");
                pitch = i;
                break;
            default:
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
