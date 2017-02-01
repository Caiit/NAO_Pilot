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
 * RobotDiscoveryListener discovers robots currently available
 * on the network.
 */

class RobotDiscoveryListener implements NsdManager.DiscoveryListener {

    private static final String TAG = "RobotDiscoveryListener";
    private ArrayList<String> robots = new ArrayList<>();
    private NsdManager nsdManager;

    /**
     * Constructor: set the nsd manager.
     */
    RobotDiscoveryListener(NsdManager nsdManager) {
        this.nsdManager = nsdManager;
    }

    /**
     * Return the robots found on the network.
     */
    ArrayList<String> getRobots() {
        return robots;
    }

    @Override
    public void onStartDiscoveryFailed(String s, int i) {
        nsdManager.stopServiceDiscovery(this);
        Log.d(TAG, "Service discovery started failed");
    }

    @Override
    public void onStopDiscoveryFailed(String s, int i) {
        nsdManager.stopServiceDiscovery(this);
        Log.d(TAG, "Service discovery stopped failed");
    }

    @Override
    public void onDiscoveryStarted(String s) {
        Log.d(TAG, "Service discovery started");
    }

    @Override
    public void onDiscoveryStopped(String s) {
        Log.d(TAG, "Service discovery stopped");
    }

    @Override
    public void onServiceFound(NsdServiceInfo service) {
        Log.d(TAG, "Service discovery success");
        if (service.getServiceType().equals("_naoqi._tcp.")) {
            // Get host from resolve listener and add to robots
            nsdManager.resolveService(service, new RobotResolveListener(robots, true));
        }
    }

    @Override
    public void onServiceLost(NsdServiceInfo service) {
        if (service.getServiceType().equals("_naoqi._tcp.")) {
            // Get host from resolve listener and remove from robots
            nsdManager.resolveService(service, new RobotResolveListener(robots, false));
        }
    }
}
