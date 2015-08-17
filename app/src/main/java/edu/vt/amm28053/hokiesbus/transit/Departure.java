package edu.vt.amm28053.hokiesbus.transit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alex on 8/11/15.
 */
public class Departure {
    private BusStop stop;
    private Date departureTime;

    private static final String DATE_FORMAT =
            "MM/dd/yyyy hh:mm:ss a";

    public Departure(BusStop stop, String date) throws ParseException {
        this(stop, new SimpleDateFormat(DATE_FORMAT).parse(date));
    }

    public Departure(BusStop stop, Date departureTime) {
        this.stop = stop;
        this.departureTime = departureTime;
    }

    public BusStop getBusStop() {
        return stop;
    }

    public String getDepartureTime() {
        return new SimpleDateFormat(DATE_FORMAT).format(departureTime);
    }

    public String getStopName() {
        return stop.getName();
    }

    public int getStopCode() {
        return stop.getCode();
    }
}
