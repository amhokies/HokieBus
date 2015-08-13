package edu.vt.amm28053.hokiesbus.utils.xml;

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
 * Created by alex on 8/13/15.
 */
public class PatternPointParser {
    private final Reader reader;

    private static final String RANK = "Rank";
    private static final String NAME = "PatternPointName";
    private static final String IS_BUS_STOP = "IsBusStop";
    private static final String STOP_CODE = "StopCode";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";

    private final String patternName;

    public PatternPointParser(Reader reader, String patternName) {
        this.reader = reader;
        this.patternName = patternName.replace(" ", "_x0020_").replace("/", "_x002F_");
    }

    /**
     * Get all buses that are on the route passed in
     * @return buses on the route
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List<BusPattern.PatternPoint> parse() throws XmlPullParserException, IOException {
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xpp.setInput(reader);
        xpp.nextTag();
        return readFeed(xpp);
    }

    private List<BusPattern.PatternPoint> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<BusPattern.PatternPoint> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, "DocumentElement");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(patternName)) {

                BusPattern.PatternPoint pp = readPatternPoint(parser);

                entries.add(pp);

            } else {
                XmlLoader.skip(parser);
            }
        }
        return entries;
    }

    private BusPattern.PatternPoint readPatternPoint(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, patternName);
        String pointName = null;
        boolean isBusStop = false;
        int stopCode = -1;
        double latitude = Double.MIN_VALUE;
        double longitude = Double.MIN_VALUE;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals(NAME)) {
                pointName = readString(parser, NAME);
            } else if (name.equals(IS_BUS_STOP)) {
                isBusStop = readBoolean(parser, IS_BUS_STOP);
            } else if (name.equals(STOP_CODE)) {
                stopCode = readInt(parser, STOP_CODE, -1);
            } else if (name.equals(LATITUDE)) {
                latitude = readDouble(parser, LATITUDE, Double.MIN_VALUE);
            } else if (name.equals(LONGITUDE)) {
                longitude = readDouble(parser, LONGITUDE, Double.MIN_VALUE);
            } else {
                XmlLoader.skip(parser);
            }
        }

        LatLng latLng = new LatLng(latitude, longitude);


        return new BusPattern.PatternPoint(pointName, latLng, isBusStop, stopCode);
    }

    private static String readString(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, tag);
        String s = XmlLoader.readText(parser);
        parser.require(XmlPullParser.END_TAG, XmlLoader.NAMESPACE, tag);
        return s;
    }

    private static boolean readBoolean(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        String s = readString(parser, tag);

        return s.startsWith("Y") ? true : false;
    }

    private static int readInt(XmlPullParser parser, String tag, int defaultValue) throws IOException, XmlPullParserException {
        String s = readString(parser, tag);

        int i = defaultValue;
        try {
            i = Integer.parseInt(s);
        } catch (NumberFormatException nfe) {}

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
