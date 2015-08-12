package edu.vt.amm28053.hokiesbus.transit;

import android.graphics.Color;
import android.location.Location;

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

    /**
     * Adds a point to the pattern
     * @param l the location of the point
     */
    public void addPoint(LatLng l) {
        mPoints.add(new PatternPoint(l));
    }

    /**
     * Adds a point to the pattern at the given index
     * @param rank the index to add at
     * @param l the location of the point
     */
    public void addPoint(int rank, LatLng l) {
        mPoints.add(rank, new PatternPoint(l));
    }

    /**
     * Draws out the pattern on a Google Map
     * @param map the Google Map to draw the pattern on
     */
    public Polyline drawPattern(GoogleMap map) {
        PolylineOptions lineOptions = new PolylineOptions();

        lineOptions.color(Color.parseColor("#ff6600"));

        for (PatternPoint point : mPoints) {
            lineOptions.add(point.mLocation);
        }

        return map.addPolyline(lineOptions);
    }

    private class PatternPoint {
        LatLng mLocation;

        private PatternPoint(LatLng l) {
            mLocation = l;
        }
    }
}
