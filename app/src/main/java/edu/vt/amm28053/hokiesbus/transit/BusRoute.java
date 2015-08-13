package edu.vt.amm28053.hokiesbus.transit;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void addDeparture(int id, String name, String time) {

        List<Departure> departuresForStop = scheduledDepartures.get(id);

        if (departuresForStop == null) {
            departuresForStop = new ArrayList<>();
            scheduledDepartures.put(id, departuresForStop);
        }

        try {
            departuresForStop.add(new Departure(new BusStop(name, id), time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addDeparture(BusStop stop, String time) {
        addDeparture(stop.getCode(), stop.getName(), time);
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

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }
}
