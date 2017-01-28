package nl.mprog.nao_pilot;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The connect fragment of the app.
 * Connect the app with the robot.
 */

public class ConnectFragment extends Fragment implements View.OnClickListener {

    View view;
    NetworkThread networkThread;
    Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_connect, container, false);
        networkThread = NetworkThread.getInstance();
        networkThread.setView(view);
//        handler = ((MainActivity) getActivity()).getHandler();
        Log.d("TEST", "onCreateView: CREATE CONNECT FRAGMENT");

        setConnectButton();
        // Set listeners
        view.findViewById(R.id.stiffBox).setOnClickListener(this);
        return view;
    }

    private void setConnectButton() {
        if (networkThread.connected()) {
            ((TextView) view.findViewById(R.id.connectButton)).setText("Disconnect");
        } else {
            ((TextView) view.findViewById(R.id.connectButton)).setText("Connect");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        setConnectButton();
        if (networkThread.connected()) {
            JSONObject json = new JSONObject();
            try {
                json.put("type", "info");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            networkThread.sendMessage(json);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stiffBox:
                CheckBox stiffness = (CheckBox) v;
                if (networkThread == null) {
                    stiffness.setChecked(false);
                } else {
                    Log.d(String.valueOf(stiffness.isChecked()), "onClick: ");
                    int value = 0;
                    if (stiffness.isChecked()) {
                        value = 1;
                    }
                    JSONObject json = new JSONObject();
                    try {
                        json.put("type", "stiffness");
                        json.put("part", "Body");
                        json.put("value", value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    networkThread.sendMessage(json);
                    Log.d(String.valueOf(json), "setStiffness: json");
                }
                break;
            default:
        }
    }
}
