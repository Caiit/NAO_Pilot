package nl.mprog.nao_pilot;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The camera fragment of the app.
 * Handles taking and saving a picture with the robot.
 */

public class CameraFragment extends Fragment implements View.OnClickListener {

    View view;
    NetworkThread networkThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_camera, container, false);
        networkThread = NetworkThread.getInstance();

        // Set button listeners
        view.findViewById(R.id.takePictureButton).setOnClickListener(this);
        view.findViewById(R.id.savePictureButton).setOnClickListener(this);
        return view;
    }

    /**
     * Handle the button clicks for taking and saving a picture.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePictureButton:
                if (networkThread != null) {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "picture");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    networkThread.addToSend(json);
                }
                break;
            case R.id.savePictureButton:
                ImageView imgView = (ImageView) view.findViewById(R.id.cameraView);
                Bitmap img = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                MediaStore.Images.Media.insertImage(view.getContext().getContentResolver(), img,
                        "robot" , "from app");
                Toast.makeText(view.getContext(), "Saved", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }
}
