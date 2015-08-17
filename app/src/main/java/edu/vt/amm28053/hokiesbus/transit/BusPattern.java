package edu.vt.amm28053.hokiesbus.transit;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 8/11/15.
 */
public class BusPattern {
    private List<PatternPoint> mPoints = new ArrayList<>(100);
    private String name;

    public BusPattern(String name) {
        this.name = name;
    }

    public void addPoint(PatternPoint p) {
        mPoints.add(p);
    }

    /**
     * Adds a point to the pattern
     * @param l the location of the point
     */
    public void addWayPoint(LatLng l) {
        addPoint(new PatternPoint("Way Point", l, false, Integer.MIN_VALUE));
    }

    /**
     * Adds a point to the pattern at the given index
     * @param l the location of the point
     */
    public void addStop(String name, LatLng l, int stopNum) {
        addPoint(new PatternPoint(name, l, true, stopNum));
    }

    public void clearPoints() {
        mPoints.clear();
    }

    public List<BusStop> getBusStops() {

        List<BusStop> stops = new ArrayList<>(30);

        for (PatternPoint p : mPoints) {
            if (p.isBusStop) {
                stops.add(new BusStop(p.name, p.stopCode, p.location));
            }
        }
        return stops;
    }

    public String getName() {
        return name;
    }

    /**
     * Draws out the pattern on a Google Map
     * @param map the Google Map to draw the pattern on
     */
    public Polyline drawPattern(GoogleMap map) {
        PolylineOptions lineOptions = new PolylineOptions();

        lineOptions.color(Color.parseColor("#ff6600"));

        for (PatternPoint point : mPoints) {
            lineOptions.add(point.location);
        }

        return map.addPolyline(lineOptions);
    }

    public static class PatternPoint {
        String name;
        boolean isBusStop;
        LatLng location;
        int stopCode;


        public PatternPoint(String s, LatLng l, boolean b, int i) {
            name = s;
            isBusStop = b;
            location = l;
            stopCode = i;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("Name ").append(name).append("\n");
            sb.append("Bus Stop: ").append(isBusStop).append("\n");
            sb.append("Location: ").append(location).append("\n");
            sb.append("Stop Code: ").append(stopCode).append("\n");

            return sb.toString();
        }
    }
}
