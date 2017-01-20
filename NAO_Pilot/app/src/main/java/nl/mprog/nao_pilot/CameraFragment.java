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

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The camera fragment of the app.
 * Take a picture with the robot.
 */

public class CameraFragment extends Fragment {

    View view;
    NetworkThread networkThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera, container, false);
        networkThread = NetworkThread.getInstance();
        networkThread.setView(view);
        handleButtons();
        return view;
    }

    private void handleButtons() {
        Button takePicture = (Button) view.findViewById(R.id.takePictureButton);
        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (networkThread != null) {
                    // TODO: add settings
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "picture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    networkThread.sendMessage(json);
                }
            }
        });

        Button showPicture = (Button) view.findViewById(R.id.savePictureButton);
        showPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (networkThread != null) {
                    Bitmap image = networkThread.getImage();
                    ImageView imgView = (ImageView) view.findViewById(R.id.cameraView);
                    if (image != null) {
                        imgView.setImageBitmap(image);
                    }
                }
            }
        });
    }

    public void setImage(Bitmap image) {
        ImageView imgView = (ImageView) view.findViewById(R.id.cameraView);
        if (image != null) {
            imgView.setImageBitmap(image);
        }
    }
}