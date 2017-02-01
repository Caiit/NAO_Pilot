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
 * TODO: TEXT HIER
 */


public class RobotResolveListener implements  NsdManager.ResolveListener {

    private ArrayList<String> robots;

    public RobotResolveListener(ArrayList robots) {
        this.robots = robots;
    }

    @Override
    public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        // Called when the resolve fails.  Use the error code to debug.
        Log.e("Failed", "Resolve failed" + errorCode);
    }

    @Override
    public void onServiceResolved(NsdServiceInfo service) {
        Log.e("Success", "Resolve Succeeded. " + service);
        robots.add(service.getHost().toString().substring(1));
    }

}