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

public class RobotDiscoveryListener implements NsdManager.DiscoveryListener {

    private ArrayList<String> robots = new ArrayList<>();
    private NsdManager nsdManager;

    public RobotDiscoveryListener(NsdManager nsdManager) {
        this.nsdManager = nsdManager;
        System.out.println("Robotdis created");
    }

    public ArrayList<String> getRobots() {
        return robots;
    }

    @Override
    public void onStartDiscoveryFailed(String s, int i) {
        nsdManager.stopServiceDiscovery(this);
        Log.d("START FAILED", "Service discovery started failed");
    }

    @Override
    public void onStopDiscoveryFailed(String s, int i) {
        nsdManager.stopServiceDiscovery(this);
        Log.d("STOP FAILED", "Service discovery stopped failed");
    }

    @Override
    public void onDiscoveryStarted(String s) {
        Log.d("START", "Service discovery started");
    }

    @Override
    public void onDiscoveryStopped(String s) {
        Log.d("STOP", "Service discovery stopped");
    }

    @Override
    public void onServiceFound(NsdServiceInfo service) {
        System.out.println("SERVICE FOUND");
        Log.d(String.valueOf(service), "Service discovery success");
        Log.d("SERVICE", "<" + service.getServiceType() + ">");
        if (service.getServiceType().equals("_naoqi._tcp.")) {
            nsdManager.resolveService(service, new RobotResolveListener(robots));
        }
        Log.d(service.getServiceName(), "onServiceFound: oeps");
    }

    @Override
    public void onServiceLost(NsdServiceInfo service) {
        if (service.getServiceType().equals("_naoqi._tcp.")) {
            if (robots.contains(service.getServiceName())) {
                robots.remove(service.getServiceName());
                System.out.println("removed " + service.getServiceName());
            }
        }
    }
}
