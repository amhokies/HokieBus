package edu.vt.amm28053.hokiesbus.transit;

import android.location.Location;

/**
 * Created by alex on 8/10/15.
 */
public class Bus {

    // The route this bus is currently on
    private BusRoute mRoute;

    // The last stop this bus stopped at
    private BusStop mLastStop;

    // The location of this bus
    private Location mLocation;

    // The number of people on this bus
    private int mPassengers;

}
