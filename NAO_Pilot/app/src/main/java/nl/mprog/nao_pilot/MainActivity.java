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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private NetworkThread networkThread;
    private Handler handler;
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

        // Create handler
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(String.valueOf(((String)msg.obj).length()), "handleMessage: msg");
                // TODO: make switch
                JSONObject message = null;
                try {
                    message = new JSONObject( (String)msg.obj);
                    if (message.getString("type").equals("picture")) {
                        setImage(message.getString("img"));
                    }
                    else {
                        Log.d(String.valueOf(message), "handleMessage: no image");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

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

    public void connectRobot(View view) {
        Button connectButton = (Button) view;
        if (networkThread == null) {
            Log.d(String.valueOf(networkThread), "connectRobot: connect");
            String IP = ((EditText) findViewById(R.id.IP)).getText().toString();

            // Start thread with robot connection
            networkThread = NetworkThread.getInstance();
            networkThread.setIP(IP);
            networkThread.setHandler(handler);
            new Thread(networkThread).start();
            connectButton.setText("Disconnect");
        } else {
            Log.d(String.valueOf(networkThread), "connectRobot: closing thread");
            // Close thread
            if (networkThread != null) {
                networkThread.closeThread();
                networkThread = null;
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

    private void setImage(String image) {
        Bitmap bitmap = null;
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
