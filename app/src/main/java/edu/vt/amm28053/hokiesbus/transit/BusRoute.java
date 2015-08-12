package edu.vt.amm28053.hokiesbus.transit;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 8/11/15.
 */
public class BusRoute {

    private String shortName;
    private String longName;

    private Map<Integer, BusStop> mScheduledStops;
    private BusPattern mPattern;

    public BusRoute(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;
        mScheduledStops = new HashMap<>();
        mPattern = new BusPattern();
    }

    public void addStop(int id, String name) {
        mScheduledStops.put(id, new BusStop(id, name));
    }

    public void addStop(BusStop stop) {
        mScheduledStops.put(stop.getId(), stop);
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }
}
