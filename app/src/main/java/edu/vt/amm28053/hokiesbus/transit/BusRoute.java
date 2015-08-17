package edu.vt.amm28053.hokiesbus.transit;

import android.view.LayoutInflater;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.vt.amm28053.hokiesbus.utils.xml.maps.StopInfoWindowAdapter;

/**
 * Created by alex on 8/11/15.
 */
public class BusRoute {

    private String shortName;
    private String longName;

    private List<Bus> buses;

    private Map<Integer, List<Departure>> scheduledDepartures;

    public BusRoute(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
        buses = new ArrayList<>();
        scheduledDepartures = new HashMap<>();
    }

    public void addDeparture(int id, String name, LatLng loc, String time) {

        List<Departure> departuresForStop = scheduledDepartures.get(id);

        if (departuresForStop == null) {
            departuresForStop = new ArrayList<>();
            scheduledDepartures.put(id, departuresForStop);
        }

        try {
            departuresForStop.add(new Departure(new BusStop(name, id, loc), time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addDeparture(BusStop stop, String time) {
        addDeparture(stop.getCode(), stop.getName(), stop.getLocation(), time);
    }

    public void addDepartures(int stopCode, List<Departure> departures) {
        scheduledDepartures.put(stopCode, departures);
    }

    public void addBus(Bus bus) {
        buses.add(bus);
    }

    public void clearBuses() {
        buses.clear();
    }

    public List<Bus> getBuses() {
        return buses;
    }

    public void drawStops(GoogleMap map, Map<String, GoogleMap.InfoWindowAdapter> adapterMap, LayoutInflater inflater) {
        for (Map.Entry<Integer, List<Departure>> e : scheduledDepartures.entrySet()) {
            int stopCode = e.getKey();
            List<Departure> departures = e.getValue();
            BusStop stop = departures.get(0).getBusStop();

            MarkerOptions markerOptions = new MarkerOptions();

            markerOptions.title(stop.getName() + " (" + stop.getCode() + ")");
            markerOptions.position(stop.getLocation());
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            StringBuilder sb = new StringBuilder();
            sb.append("Route: ").append(shortName);

            for (Departure d : departures) {
                sb.append("\n").append(d.getDepartureTime());
            }
            markerOptions.snippet(sb.toString());

            Marker m = map.addMarker(markerOptions);

            adapterMap.put(m.getId(), new StopInfoWindowAdapter(inflater));
        }
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }
}
