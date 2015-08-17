package edu.vt.amm28053.hokiesbus.utils.xml;

import android.os.Debug;
import android.util.Log;
import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import edu.vt.amm28053.hokiesbus.transit.Bus;
import edu.vt.amm28053.hokiesbus.transit.BusPattern;
import edu.vt.amm28053.hokiesbus.transit.BusStop;

/**
 * Created by alex on 8/12/15.
 */
public class BusParser {
    private final Reader reader;

    private static final String LATEST_INFO_TABLE = "LatestInfoTable";
    private static final String ROUTE_SHORT_NAME = "RouteShortName";
    private static final String PATTERN_NAME = "PatternName";
    private static final String LAST_STOP_NAME = "LastStopName";
    private static final String LAST_STOP_CODE = "StopCode";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String PASSENGERS = "TotalCount";


    public BusParser(Reader reader) {
        this.reader = reader;
    }

    /**
     * Get all buses that are on the route passed in
     * @param routeShortName shortname for the route to search for buses on
     * @return buses on the route
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List<Bus> parse(String routeShortName) throws XmlPullParserException, IOException {
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xpp.setInput(reader);
        xpp.nextTag();
        return readFeed(xpp, routeShortName);
    }

    private static List<Bus> readFeed(XmlPullParser parser, String route) throws XmlPullParserException, IOException {
        List<Bus> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, "DocumentElement");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(LATEST_INFO_TABLE)) {

                Bus b = readBus(parser);

                if (b != null && b.getRouteShortName().equals(route)) {
                    entries.add(b);
                }
            } else {
                XmlLoader.skip(parser);
            }
        }
        return entries;
    }

    private static Bus readBus(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, LATEST_INFO_TABLE);
        String shortName = null;
        String patternName = null;
        String lastStopName = null;
        int lastStopCode = -1;
        double latitude = Double.MIN_VALUE;
        double longitude = Double.MIN_VALUE;
        int passengers = -1;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals(ROUTE_SHORT_NAME)) {
                shortName = readString(parser, ROUTE_SHORT_NAME);
            } else if (name.equals(PATTERN_NAME)) {
                patternName = readString(parser, PATTERN_NAME);
            } else if (name.equals(LAST_STOP_NAME)) {
                lastStopName = readString(parser, LAST_STOP_NAME);
            } else if (name.equals(LAST_STOP_CODE)) {
                lastStopCode = readInt(parser, LAST_STOP_CODE, -1);
            } else if (name.equals(LATITUDE)) {
                latitude = readDouble(parser, LATITUDE, Double.MIN_VALUE);
            } else if (name.equals(LONGITUDE)) {
                longitude = readDouble(parser, LONGITUDE, Double.MIN_VALUE);
            } else if (name.equals(PASSENGERS)) {
                passengers = readInt(parser, PASSENGERS, -1);
            } else {
                XmlLoader.skip(parser);
            }
        }

        if (shortName == null) return null;

        BusPattern bp = new BusPattern(patternName);
        LatLng latLng = new LatLng(latitude, longitude);
        BusStop bs = new BusStop(lastStopName, lastStopCode, latLng);

        return new Bus(shortName, bs, latLng, passengers, bp);
    }

    private static String readString(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, tag);
        String s = XmlLoader.readText(parser);
        parser.require(XmlPullParser.END_TAG, XmlLoader.NAMESPACE, tag);
        return s;
    }

    private static int readInt(XmlPullParser parser, String tag, int defaultValue) throws IOException, XmlPullParserException {
        String s = readString(parser, tag);

        int i = defaultValue;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            Log.e("HokieBus", nfe.getMessage());
        }

        return i;
    }

    private static double readDouble(XmlPullParser parser, String tag, double defaultValue) throws IOException, XmlPullParserException {
        String s = readString(parser, tag);

        double d = defaultValue;
        try {
            d = Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            Log.e("HokieBus", nfe.getMessage());
        }

        return d;
    }
}
