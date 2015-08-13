package edu.vt.amm28053.hokiesbus.transit;

import android.view.LayoutInflater;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import edu.vt.amm28053.hokiesbus.R;
import edu.vt.amm28053.hokiesbus.utils.xml.maps.BusInfoWindowAdapter;

/**
 * Created by alex on 8/10/15.
 */
public class Bus {

    // Shortname of the route it's on
    private String routeShortName;

    // The last stop this bus stopped at
    private BusStop mLastStop;

    // The location of this bus
    private LatLng mLocation;

    // The number of people on this bus
    private int mPassengers;

    // The pattern this bus is on
    private BusPattern pattern;

    public Bus(String s, BusStop bs, LatLng l, int i, BusPattern bp) {
        routeShortName = s;
        mLastStop = bs;
        mLocation = l;
        mPassengers = i;
        pattern = bp;
    }

    public String getRouteShortName() {
        return routeShortName;
    }

    public String getPatternName() {
        return pattern.getName();
    }

    public void drawPattern(GoogleMap map) {
        pattern.drawPattern(map);
    }

    public void drawBus(GoogleMap map, Map<String, GoogleMap.InfoWindowAdapter> adapterMap, LayoutInflater inflater) {
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.title(pattern.getName() + " (" + routeShortName + ")");
        markerOptions.position(mLocation);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location));

        StringBuilder snippet = new StringBuilder();

        // add the pattern name
        snippet.append("Pattern: ").append(pattern.getName()).append("\n");

        // add last stop
        snippet.append("Last Stop: ").append(mLastStop.getName()).append( "(").append(mLastStop.getCode()).append(")").append("\n");

        // add passenger count
        snippet.append("Passengers: ").append(mPassengers);

        markerOptions.snippet(snippet.toString());

        Marker m = map.addMarker(markerOptions);

        adapterMap.put(m.getId(), new BusInfoWindowAdapter(inflater));
    }

    public void addPatternPoint(BusPattern.PatternPoint point) {
        pattern.addPoint(point);
    }

    public void clearPatternPoints() {
        pattern.clearPoints();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("On route: ").append(routeShortName).append("\n");
        sb.append("Pattern: ").append(pattern.getName()).append("\n");
        sb.append("Location: ").append(mLocation.latitude).append(", ").append(mLocation.longitude).append("\n");
        sb.append("Last Stop: ").append(mLastStop.getName()).append("(").append(mLastStop.getCode()).append(")").append("\n");
        sb.append("Number of passengers: ").append(mPassengers).append("\n");

        return sb.toString();
    }
}
