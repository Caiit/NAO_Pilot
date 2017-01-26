package nl.mprog.nao_pilot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The moves fragment of the app.
 * Take a picture with the robot.
 */

public class MovesFragment extends Fragment implements View.OnClickListener {

    View view;
    NetworkThread networkThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_moves, container, false);
        networkThread = NetworkThread.getInstance();
        networkThread.setView(view);
//        handleButtons();

        // Set button listeners
        view.findViewById(R.id.waveButton).setOnClickListener(this);
        view.findViewById(R.id.guitarButton).setOnClickListener(this);

        return view;
    }

//    private void handleButtons() {
//        ImageButton waveButton = (ImageButton) view.findViewById(R.id.waveButton);
//        waveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (networkThread != null) {
//                     JSONObject json = new JSONObject();
//                    try {
//                        InputStream is = getContext().getAssets().open("wave.txt");
//                        int size = is.available();
//                        byte[] byteFile = new byte[size];
//                        is.read(byteFile);
//                        is.close();
//                        // Convert the buffer into a string.
//                        String file = new String(byteFile);
//                        json.put("type", "moves");
//                        json.put("file", file);
//                        System.out.println(file.length());
//                        networkThread.sendMessage(json);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//    }

    @Override
    public void onClick(View view) {
        String fileName = "";
        switch (view.getId()) {
            // TODO: logische knopjes maken en function toevoegen
            case R.id.waveButton:
                fileName = "wave.txt";
                break;
            case R.id.guitarButton:
                fileName = "guitar.txt";
                break;
            default:
                break;
        }

        if (networkThread != null && !fileName.equals("")) {
            JSONObject json = new JSONObject();
            try {
                InputStream is = getContext().getAssets().open(fileName);
                int size = is.available();
                byte[] byteFile = new byte[size];
                is.read(byteFile);
                is.close();
                // Convert the buffer into a string.
                String file = new String(byteFile);
                json.put("type", "moves");
                json.put("file", file);
                System.out.println(file.length());
                networkThread.sendMessage(json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}