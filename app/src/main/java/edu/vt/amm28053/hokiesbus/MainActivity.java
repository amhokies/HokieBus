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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import edu.vt.amm28053.hokiesbus.fragments.MapFragment;
import edu.vt.amm28053.hokiesbus.fragments.MapRetained;
import edu.vt.amm28053.hokiesbus.fragments.SecondFragment;
import edu.vt.amm28053.hokiesbus.fragments.ThirdFragment;
import edu.vt.amm28053.hokiesbus.services.BTUpdateService;
import edu.vt.amm28053.hokiesbus.transit.BusRoute;
import edu.vt.amm28053.hokiesbus.transit.adapter.RouteAdapter;

public class MainActivity extends AppCompatActivity implements ServiceConnection, MapRetained.MapRetainedListener, MapFragment.MapFragmentListener {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nv;

    // Fragments
    private MapFragment map;
    private SecondFragment two;
    private ThirdFragment three;

    private MapRetained mapRetained;

    private static final String MAP_TAG = "Fragment.MAP";
    private static final String TWO_TAG = "Fragment.TWO";
    private static final String THREE_TAG = "Fragment.THREE";
    private static final String RETAINED_MAP_TAG="Fragment.MAP.RETAINED";

    private static final String CURR_FRAG_BUNDLE = "CURR_FRAG";

    private String currentFragmentTag;

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


        currentFragmentTag = MAP_TAG;

        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString(CURR_FRAG_BUNDLE, MAP_TAG);
        }


        mapRetained = (MapRetained)getSupportFragmentManager().findFragmentByTag(RETAINED_MAP_TAG);

        if (mapRetained == null) {
            mapRetained = new MapRetained();
            getSupportFragmentManager().beginTransaction().add(mapRetained, RETAINED_MAP_TAG).commit();
        }

        showFragment(currentFragmentTag);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString(CURR_FRAG_BUNDLE, currentFragmentTag);

        super.onSaveInstanceState(outState);
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

        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                tag = MAP_TAG;
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

    private void showFragment(String tag) {
        FragmentManager fragMan = getSupportFragmentManager();

        Fragment f = fragMan.findFragmentByTag(tag);

        switch (tag) {
            case MAP_TAG:
                f = map = (MapFragment)((f == null) ? new MapFragment() : f);
                break;
            case TWO_TAG:
                f = two = (SecondFragment)((f == null) ? new SecondFragment() : f);
                break;
            case THREE_TAG:
                f = three = (ThirdFragment)((f == null) ? new ThirdFragment() : f);
                break;
        }

        fragMan.beginTransaction().replace(R.id.flContent, f, tag).commit();

        currentFragmentTag = tag;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("HokieBus", name.toShortString() + " connected to MainActivity");

        BTUpdateService btService = ((BTUpdateService.LocalBinder) service).getServiceInstance();
        btService.bindToActivity(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("HokieBus", name.toShortString() + " disconnected from MainActivity");
    }

    @Override
    public void onLoadRoutesInit(List<BusRoute> routes) {
        if (map != null && currentFragmentTag.equals(MAP_TAG)) {
            map.updateRoutes(routes);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
}
