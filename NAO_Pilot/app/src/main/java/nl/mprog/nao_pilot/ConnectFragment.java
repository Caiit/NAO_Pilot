package nl.mprog.nao_pilot;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_connect, container, false);
        networkThread = NetworkThread.getInstance();
        mainActivity = (MainActivity) getActivity();
        Log.d("CONNECT FRAGMENT", "onCreateView: ON CREATE VIEW");
        // Handle the connectbutton and info
        setConnectButton();

        // Set dropdown
        setRobotsDropdown();

        // Set listeners
        view.findViewById(R.id.stiffBox).setOnClickListener(this);
        return view;
    }


    private void setRobotsDropdown() {
        Spinner dropdown = (Spinner) view.findViewById(R.id.robotsDropdown);
        ArrayList<String> robots = mainActivity.getRobots();
        System.out.println(robots);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, robots);
        adapter.setNotifyOnChange(true);
        dropdown.setAdapter(adapter);
    }


    private void setConnectButton() {
        if (networkThread != null && networkThread.connected()) {
            ((TextView) view.findViewById(R.id.connectButton)).setText("Disconnect");
        } else {
            ((TextView) view.findViewById(R.id.connectButton)).setText("Connect");
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (view != null) {
            setConnectButton();
            mainActivity.showInfo();
        }
        if (networkThread != null && networkThread.connected()) {
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
                if (networkThread == null || !networkThread.connected()) {
                    stiffness.setChecked(false);
                } else {
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
                }
                break;
            default:
        }
    }
}
