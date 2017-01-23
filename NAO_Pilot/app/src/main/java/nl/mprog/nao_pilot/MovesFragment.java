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

public class MovesFragment extends Fragment {

    View view;
    NetworkThread networkThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_moves, container, false);
        networkThread = NetworkThread.getInstance();
        networkThread.setView(view);
        handleButtons();
        return view;
    }

    private void handleButtons() {
        ImageButton waveButton = (ImageButton) view.findViewById(R.id.waveButton);
        waveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (networkThread != null) {
                    // Read in file
//                    byte[] byteFile = new byte[0];
//                    try {
//                        File file = new File(getContext().getAssets() + "wave.txt");
//                        Log.d(String.valueOf(file), "onClick: file");
//                        byteFile  = new byte [(int)file.length()];
//                        FileInputStream fis = new FileInputStream(file);
//                        BufferedInputStream bis = new BufferedInputStream(fis);
//                        bis.read(byteFile, 0, byteFile.length);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }


                    JSONObject json = new JSONObject();
                    try {
                        InputStream is = getContext().getAssets().open("wave.txt");
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
        });
    }
}