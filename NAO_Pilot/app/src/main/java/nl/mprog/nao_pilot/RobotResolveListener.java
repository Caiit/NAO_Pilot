package nl.mprog.nao_pilot;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.util.ArrayList;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * RobotResolve finds the robots IP given the discovered service.
 */


class RobotResolveListener implements  NsdManager.ResolveListener {

    private static final String TAG = "RobotResolveListener";
    private ArrayList<String> robots;
    private boolean add;

    /**
     * Constructor: set the robots arraylist and
     * if the robot needs to be added or not.
     */
    RobotResolveListener(ArrayList robots, boolean add) {
        this.robots = robots;
        this.add = add;
    }

    @Override
    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        // Called when the resolve fails.  Use the error code to debug.
        Log.e(TAG, "Resolve failed" + errorCode);
    }

    @Override
    public void onServiceResolved(NsdServiceInfo service) {
        Log.e(TAG, "Resolve Succeeded. " + service);
        String IP = service.getHost().toString().substring(1);
        // Add or remove robot
        if (add && !robots.contains(IP)) {
            robots.add(IP);
        } else {
            if (robots.contains(IP)) {
                robots.remove(IP);
            }
        }
    }

}