package nl.mprog.nao_pilot;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The speak fragment of the app.
 * Let the robot say the appropriate text.
 */

public class SpeakFragment extends Fragment {

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_speak, container, false);
        handleSeekbars();
        return view;
    }

    private void handleSeekbars() {
        SeekBar volumeBar = (SeekBar) view.findViewById(R.id.seekBarVolume);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                TextView volumeText = (TextView) getActivity().findViewById(R.id.volumeText);
                volumeText.setText("Volume: " + String.valueOf(progress) + " %");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        SeekBar speedBar = (SeekBar) view.findViewById(R.id.seekBarSpeed);
        speedBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                TextView speedText = (TextView) getActivity().findViewById(R.id.speedText);
                speedText.setText("Speed: " + String.valueOf(progress + 50) + " %");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        SeekBar pitchBar = (SeekBar) view.findViewById(R.id.seekBarPitch);
        pitchBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                TextView pitchText = (TextView) getActivity().findViewById(R.id.pitchText);
                pitchText.setText("Pitch: " + String.valueOf(progress + 50) + " %");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

}
