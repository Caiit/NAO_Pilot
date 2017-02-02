package nl.mprog.nao_pilot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.nsd.NsdManager;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * The main activity of the app.
 * Generates the tabs used by the fragments.
 * Handles the network connection with the robot:
 *  connects/disconnects the robot
 *  handles messages from the robot
 */

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private NetworkThread networkThread;
    private Handler handler;
    private ViewPager viewPager;
    private RobotDiscoveryListener robotDiscovery;
    private String name = "";
    private int battery = 0;
    private boolean stiffness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add tabs
        addTabs();

        // Create handler
        setHandler();

        // Get robots on network
        NsdManager nsdManager = (NsdManager) getSystemService(NSD_SERVICE);
        robotDiscovery = new RobotDiscoveryListener(nsdManager);
        nsdManager.discoverServices("_naoqi._tcp.", NsdManager.PROTOCOL_DNS_SD, robotDiscovery);
    }


    /**
     * Onclick from the connect button.
     *
     * Connect with the robot if it is not connected, otherwise disconnect.
     */
    public void connectRobot(View view) {
        Button connectButton = (Button) view;
        LinearLayout robotInfo = (LinearLayout) findViewById(R.id.robotInfo);
        if (networkThread == null || !networkThread.connected()) {
            // Get IP from dropdown list
            Spinner dropdown = (Spinner) findViewById(R.id.robotsDropdown);
            String IP = (String) dropdown.getSelectedItem();
            if (IP == null) {
                return;
            }
            // Start thread with robot connection
            networkThread = NetworkThread.getInstance();
            networkThread.setIP(IP);
            networkThread.setHandler(handler);
            new Thread(networkThread).start();

            // Change button text and show robot info if connected
            if (networkThread != null && networkThread.connected()) {
                // Get info from the robot
                JSONObject json = new JSONObject();
                try {
                    json.put("type", "info");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                networkThread.addToSend(json);

                connectButton.setText("Disconnect");
                robotInfo.setVisibility(View.VISIBLE);
            }
        } else {
            // Close thread
            if (networkThread != null) {
                networkThread.closeThread();
            }
            // Change button text and hide robot info
            connectButton.setText("Connect");
            robotInfo.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Show the robot info: name, battery and stiffness status.
     */
    public void showInfo() {
        TextView nameTV = (TextView) findViewById(R.id.nameText);
        if (nameTV == null) {
            // Fragment is not loaded yet
            return;
        }
        nameTV.setText("Name: " + name);
        TextView batteryTV = (TextView) findViewById(R.id.batteryText);
        batteryTV.setText("Battery: " + battery);
        CheckBox stiffBox = (CheckBox) findViewById(R.id.stiffBox);
        stiffBox.setChecked(stiffness);

        LinearLayout robotInfo = (LinearLayout) findViewById(R.id.robotInfo);
        if (robotInfo == null) {
            return;
        }
        if (networkThread != null && networkThread.connected()) {
            robotInfo.setVisibility(View.VISIBLE);
        } else {
            robotInfo.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Get the robots found on the current network.
     */
    public ArrayList<String> getRobots() {
        return robotDiscovery.getRobots();
    }

    /**
     * Set the handler that handles messages from the networkthread.
     */
    private void setHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                JSONObject message;
                try {
                    message = new JSONObject((String)msg.obj);
                    switch (message.getString("type")) {
                        case "picture":
                            setImage(message.getString("img"));
                            break;
                        case "info":
                            name = message.getString("name");
                            battery = Integer.parseInt(message.getString("battery"));
                            stiffness = Boolean.parseBoolean(message.getString("stiffness"));
                            showInfo();
                            break;
                        case "disconnect":
                            Toast.makeText(getApplicationContext(), message.getString("text"),
                                    Toast.LENGTH_SHORT).show();
                            // Change connect button and hide robot info
                            Button connectButton = (Button) findViewById(R.id.connectButton);
                            LinearLayout robotInfo = (LinearLayout) findViewById(R.id.robotInfo);
                            if (connectButton != null) {
                                connectButton.setText("Connect");
                                robotInfo.setVisibility(View.INVISIBLE);
                            }
                            break;
                        case "error":
                            Toast.makeText(getApplicationContext(), message.getString("text"),
                                    Toast.LENGTH_SHORT).show();
                            break;
                        default:
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Show the image obtained from the robot.
     */
    private void setImage(String image) {
        Bitmap bitmap;
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            Log.d("No valid image", "setImage: Failed");
            return;
        }

        ImageView imgView = (ImageView) findViewById(R.id.cameraView);
        if (imgView != null && image != null) {
            imgView.setImageBitmap(bitmap);
        }
    }

    /**
     * Add the fragment tabs to the actionbar.
     */
    private void addTabs() {
        TabsPagerAdapter tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(tabsAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.connect);
        tabLayout.getTabAt(1).setIcon(R.drawable.speak);
        tabLayout.getTabAt(2).setIcon(R.drawable.walk);
        tabLayout.getTabAt(3).setIcon(R.drawable.camera);
        tabLayout.getTabAt(4).setIcon(R.drawable.moves);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
