package edu.vt.amm28053.hokiesbus.transit;

/**
 * Created by alex on 8/11/15.
 */
public class BusStop {
    private int stopID;
    private String stopName;

    public BusStop(int id, String name) {
        this.stopID = id;
        this.stopName = name;
    }

    public int getId() {
        return stopID;
    }

    public String getName() {
        return stopName;
    }
}
