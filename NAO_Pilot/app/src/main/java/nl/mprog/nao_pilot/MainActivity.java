package nl.mprog.nao_pilot;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private NetworkThread networkThread;
    ViewPager viewPager;

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
            networkThread = new NetworkThread(IP);
            new Thread(networkThread).start();
            connectButton.setText("Disconnect");
        } else {
            // Close thread
            if (networkThread != null) {
                networkThread.closeThread();
            }
            connectButton.setText("Connect");
        }
    }

    public void sayText(View view) {
        if (networkThread != null) {
            String message = ((EditText) findViewById(R.id.sayText)).getText().toString();
            networkThread.sendMessage(message);
        }
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
