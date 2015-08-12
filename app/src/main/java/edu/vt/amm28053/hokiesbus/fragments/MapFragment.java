package edu.vt.amm28053.hokiesbus.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.List;

import edu.vt.amm28053.hokiesbus.R;
import edu.vt.amm28053.hokiesbus.transit.BusPattern;
import edu.vt.amm28053.hokiesbus.transit.BusRoute;
import edu.vt.amm28053.hokiesbus.transit.adapter.RouteAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;

    private SlidingUpPanelLayout mLayout;

    private MapView mapView;
    private ListView routeList;
    private TextView currentRouteText;
    private ImageView panelStateImage;

    private RouteAdapter routeAdapter;

    private MapFragmentListener listener;

    private static final String CURR_ROUTE_LONG_BUNDLE = "CURR_ROUTE_LONG";
    private static final String CURR_ROUTE_SHORT_BUNDLE = "CURR_ROUTE_SHORT";
    private static final String PANEL_STATE_EXPANDED = "PANEL_STATE_EXPANDED";
    private static final String MAP_STATE_BUNDLE = "MAP_STATE";


    private BusRoute currentBusRoute = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (MapFragmentListener)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView)v.findViewById(R.id.map);

        currentRouteText = (TextView)v.findViewById(R.id.selectedRoute);

        mLayout = (SlidingUpPanelLayout)v.findViewById(R.id.sliding_layout);
        panelStateImage = (ImageView)mLayout.findViewById(R.id.slide_state_image);

        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float v) {

            }

            @Override
            public void onPanelCollapsed(View panel) {
                panelStateImage.setImageResource(R.drawable.drag_up);
            }

            @Override
            public void onPanelExpanded(View panel) {
                panelStateImage.setImageResource(R.drawable.drag_down);
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }
        });

        routeList = (ListView)v.findViewById(R.id.routeList);
        routeAdapter = new RouteAdapter(listener.getContext(), new ArrayList<BusRoute>());

        routeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                currentBusRoute = (BusRoute)adapter.getItemAtPosition(position);
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                currentRouteText.setText(currentBusRoute.getLongName());
            }
        });

        routeList.setAdapter(routeAdapter);

        mapView.getMapAsync(this);

        Bundle mapState = null;
        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(PANEL_STATE_EXPANDED, false))
                panelStateImage.setImageResource(R.drawable.drag_down);

            String shortName = savedInstanceState.getString(CURR_ROUTE_SHORT_BUNDLE, null);
            String longName = savedInstanceState.getString(CURR_ROUTE_LONG_BUNDLE, null);

            if (shortName != null && longName != null) {
                currentBusRoute = new BusRoute(shortName, longName);
                currentRouteText.setText(currentBusRoute.getLongName());
            }

            // Load the map state bundle from the main savedInstanceState
            mapState = savedInstanceState.getBundle(MAP_STATE_BUNDLE);
        }

        mapView.onCreate(mapState);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        Bundle mapState = new Bundle();
        mapView.onSaveInstanceState(mapState);
        outState.putBundle(MAP_STATE_BUNDLE, mapState);

        if (currentBusRoute != null) {
            outState.putString(CURR_ROUTE_SHORT_BUNDLE, currentBusRoute.getShortName());
            outState.putString(CURR_ROUTE_LONG_BUNDLE, currentBusRoute.getLongName());
        }

        if (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED)
            outState.putBoolean(PANEL_STATE_EXPANDED, true);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void updateRoutes(List<BusRoute> routes) {
        routeAdapter.clear();
        routeAdapter.addAll(routes);
        routeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("HokieBus", "Google Map loaded");
        this.map = map;

        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        LatLng blacksburg = new LatLng(37.23, -80.417778);

        // Updates the location and zoom of the MapView
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(blacksburg, 14));

        BusPattern bp = new BusPattern();
        bp.addPoint(new LatLng(37.2285, -80.42286));
        bp.addPoint(new LatLng(37.22777, -80.42396));
        bp.addPoint(new LatLng(37.2269, -80.4246));
        bp.addPoint(new LatLng(37.22645, -80.42529));
        bp.addPoint(new LatLng(37.2289, -80.42745));
        bp.addPoint(new LatLng(37.23045, -80.42864));
        bp.addPoint(new LatLng(37.23148, -80.42883));
        bp.addPoint(new LatLng(37.23133, -80.42998));
        bp.addPoint(new LatLng(37.23168, -80.43289));
        bp.addPoint(new LatLng(37.23163, -80.43419));
        bp.drawPattern(map);
    }

    public interface MapFragmentListener {
        Context getContext();
    }
}
