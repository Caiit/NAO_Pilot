package nl.mprog.nao_pilot;

import android.app.Activity;
import android.content.Context;
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

public class CameraFragment extends Fragment implements View.OnClickListener {

    View view;
    NetworkThread networkThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera, container, false);
        networkThread = NetworkThread.getInstance();
        networkThread.setView(view);

        Log.d("CREATE CAMERA FRAGEMNT", "onCreateView: ");

        // Set button listeners
        view.findViewById(R.id.takePictureButton).setOnClickListener(this);
        view.findViewById(R.id.savePictureButton).setOnClickListener(this);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("TEST CAMERA", "setUserVisibleHint: ");
        if (networkThread != null) {
            JSONObject json = new JSONObject();
            try {
                json.put("type", "picture");
                if (isVisibleToUser) {
                    Log.d("start picture", "setUserVisibleHint: visible");
                    // TODO: add settings
                    json.put("get", "true");
                } else {
                    Log.d("stop picture", "setUserVisibleHint: not visible");
                    json.put("get", "false");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            networkThread.sendMessage(json);
        }
    }

//    private void handleButtons() {
//        Button takePicture = (Button) view.findViewById(R.id.takePictureButton);
//        takePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (networkThread != null) {
//                    // TODO: add settings
//                    JSONObject json = new JSONObject();
//                    try {
//                        json.put("type", "picture");
//                        json.put("get", "true");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    networkThread.sendMessage(json);
//                }
//            }
//        });
//
//        Button savePicture = (Button) view.findViewById(R.id.savePictureButton);
//        savePicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (networkThread != null) {
//                    // TODO: save picture
//                }
//            }
//        });
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // TODO: logische knopjes maken en function toevoegen
            case R.id.takePictureButton:
                Log.d("CHeeese", "onClick: take picture");
                break;
            case R.id.savePictureButton:
                Log.d("Save", "onClick: save picture");
                break;
            default:
        }
    }
}
