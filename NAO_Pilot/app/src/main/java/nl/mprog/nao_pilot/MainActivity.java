package nl.mprog.nao_pilot;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private NetworkThread networkThread;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Add tabs
        addTabs();
    }

    private void addTabs() {
        TabsPagerAdapter tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(tabsAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void connectRobot(View view) {
        Button connectButton = (Button) view;
        if (connectButton.getText().equals("Connect")) {
            String IP = ((EditText) findViewById(R.id.IP)).getText().toString();

            // Start thread with robot connection
            networkThread = NetworkThread.getInstance();
            networkThread.setIP(IP);
            new Thread(networkThread).start();
            connectButton.setText("Disconnect");
        } else {
            Log.d(String.valueOf(networkThread), "connectRobot: closing thread");
            // Close thread
            if (networkThread != null) {
                networkThread.closeThread();
            }
            connectButton.setText("Connect");
        }
    }

    public void setStiffness(View view) {
        CheckBox stiffness = (CheckBox) view;
        if (networkThread != null) {
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
        } else {
            stiffness.setChecked(false);
        }
    }

    public void sayText(View view) {
        if (networkThread != null) {
            String message = ((EditText) findViewById(R.id.sayText)).getText().toString();
            String volumeText = (String) ((TextView) findViewById(R.id.volumeText)).getText();
            String volume = volumeText.split("\\s+")[1];
            String speedText = (String) ((TextView) findViewById(R.id.speedText)).getText();
            String speed = speedText.split("\\s+")[1];
            String pitchText = (String) ((TextView) findViewById(R.id.pitchText)).getText();
            String pitch = pitchText.split("\\s+")[1];

            JSONObject json = new JSONObject();
            try {
                json.put("type", "speak");
                json.put("text", message);
                json.put("volume", volume);
                json.put("speed", speed);
                json.put("pitch", pitch);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            networkThread.sendMessage(json);
        }
    }

    /**************** TABS *******************/

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
