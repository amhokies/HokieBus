package edu.vt.amm28053.hokiesbus;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import edu.vt.amm28053.hokiesbus.fragments.FirstFragment;
import edu.vt.amm28053.hokiesbus.fragments.SecondFragment;
import edu.vt.amm28053.hokiesbus.fragments.ThirdFragment;
import edu.vt.amm28053.hokiesbus.services.BTUpdateService;

public class MainActivity extends AppCompatActivity implements ServiceConnection {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nv;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Fragments
    private FirstFragment one;
    private SecondFragment two;
    private ThirdFragment three;

    private static final String ONE_TAG = "Fragment.ONE";
    private static final String TWO_TAG = "Fragment.TWO";
    private static final String THREE_TAG = "Fragment.THREE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("HokieBus", "OnCreate called from thread " + Thread.currentThread().getName());

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Set the menu icon instead of the launcher icon.
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        nv = (NavigationView) findViewById(R.id.nvView);
        setupDrawerContent(nv);

        showFragment(ONE_TAG);

        Intent serviceIntent = new Intent(this, BTUpdateService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(this, BTUpdateService.class);
        bindService(serviceIntent, this, Context.BIND_IMPORTANT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Intent serviceIntent = new Intent(this, BTUpdateService.class);
        unbindService(this);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }



    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        String tag = null;

        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                tag = ONE_TAG;
                break;
            case R.id.nav_second_fragment:
                tag = TWO_TAG;
                break;
            case R.id.nav_third_fragment:
                tag = THREE_TAG;
                break;
        }

        showFragment(tag);

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateActivity(int res) {
        one.setTextView(String.valueOf(res));
    }

    private void showFragment(String tag) {
        FragmentManager fragMan = getSupportFragmentManager();

        Fragment f = fragMan.findFragmentByTag(tag);

        if (f == null) {
            switch (tag) {
                case ONE_TAG:
                    f = one = new FirstFragment();
                    break;
                case TWO_TAG:
                    f = two = new SecondFragment();
                    break;
                case THREE_TAG:
                    f = three =  new ThirdFragment();
                    break;
            }
        }

        fragMan.beginTransaction().replace(R.id.flContent, f, tag).commit();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("HokieBus", name.toShortString() + " connected to MainActivity");

        BTUpdateService btService = ((BTUpdateService.LocalBinder)service).getServiceInstance();
        btService.bindToActivity(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("HokieBus", name.toShortString() + " disconnected from MainActivity");
    }
}
