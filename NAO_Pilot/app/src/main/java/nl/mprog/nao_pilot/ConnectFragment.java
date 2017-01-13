package nl.mprog.nao_pilot;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The connect fragment of the app.
 * Connect the app with the robot.
 */

public class ConnectFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_connect, container, false);
    }
}
