package nl.mprog.nao_pilot;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private NetworkThread networkThread;
    private Handler handler;
    private ViewPager viewPager;
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
            String IP = ((EditText) findViewById(R.id.IP)).getText().toString();

            // Start thread with robot connection
            networkThread = NetworkThread.getInstance();
            networkThread.setIP(IP);
            networkThread.setHandler(handler);
            new Thread(networkThread).start();

            // Get info from the robot
            JSONObject json = new JSONObject();
            try {
                json.put("type", "info");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            networkThread.sendMessage(json);
            // Change button text and show robot info
            connectButton.setText("Disconnect");
            robotInfo.setVisibility(View.VISIBLE);
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
        if (networkThread != null && networkThread.connected() && robotInfo != null) {
            robotInfo.setVisibility(View.VISIBLE);
        } else {
            robotInfo.setVisibility(View.INVISIBLE);
        }
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
     * Set the handler that handles messages from the networkthread.
     */
    private void setHandler() {
        // TODO: error messages handlen
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
                            Log.d(message.getString("stiffness"), "handleMessage: ");
                            stiffness = Boolean.parseBoolean(message.getString("stiffness"));
                            showInfo();
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
