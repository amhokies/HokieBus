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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.vt.amm28053.hokiesbus.R;
import edu.vt.amm28053.hokiesbus.transit.Bus;
import edu.vt.amm28053.hokiesbus.transit.BusPattern;
import edu.vt.amm28053.hokiesbus.transit.BusRoute;
import edu.vt.amm28053.hokiesbus.transit.adapter.RouteAdapter;
import edu.vt.amm28053.hokiesbus.utils.xml.maps.MainInfoWindowAdapter;

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

    private Map<String, GoogleMap.InfoWindowAdapter> adapterMap;

    private BusRoute currentBusRoute = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (MapFragmentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        adapterMap = new HashMap<>();

        mapView = (MapView) v.findViewById(R.id.map);

        currentRouteText = (TextView) v.findViewById(R.id.selectedRoute);

        mLayout = (SlidingUpPanelLayout) v.findViewById(R.id.sliding_layout);
        panelStateImage = (ImageView) mLayout.findViewById(R.id.slide_state_image);

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

        routeList = (ListView) v.findViewById(R.id.routeList);
        routeAdapter = new RouteAdapter(listener.getContext(), new ArrayList<BusRoute>());

        routeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                currentBusRoute = (BusRoute) adapter.getItemAtPosition(position);
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                currentRouteText.setText(currentBusRoute.getLongName());
                listener.onRouteSelected(currentBusRoute);
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

    public void showRoute(BusRoute route) {

        if (map != null) {

            map.clear();
            adapterMap.clear();

            for (Bus b : route.getBuses()) {
                b.drawBus(map, adapterMap, getActivity().getLayoutInflater());
                b.drawPattern(map);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d("HokieBus", "Google Map loaded");
        this.map = map;

        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        map.setInfoWindowAdapter(new MainInfoWindowAdapter(adapterMap));

        LatLng blacksburg = new LatLng(37.23, -80.417778);

        // Updates the location and zoom of the MapView
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(blacksburg, 14));
    }

    public interface MapFragmentListener {
        Context getContext();

        void onRouteSelected(BusRoute route);
    }
}
