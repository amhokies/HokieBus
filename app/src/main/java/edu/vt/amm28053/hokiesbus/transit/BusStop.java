package edu.vt.amm28053.hokiesbus.transit;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by alex on 8/12/15.
 */
public class BusStop {
    private final String name;
    private final int code;
    private LatLng location;


    public BusStop(String name, int code, LatLng location) {
        this.name = name;
        this.code = code;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public LatLng getLocation() {
        return location;
    }
}
