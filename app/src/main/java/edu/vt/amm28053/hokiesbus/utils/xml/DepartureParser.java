package edu.vt.amm28053.hokiesbus.utils.xml;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import edu.vt.amm28053.hokiesbus.transit.BusStop;
import edu.vt.amm28053.hokiesbus.transit.Departure;

/**
 * Created by alex on 8/13/15.
 */
public class DepartureParser {
    private final Reader reader;
    private final BusStop stop;

    private static final String NEXT_DEPARTURES = "NextDepartures";
    private static final String TIME = "AdjustedDepartureTime";

    public DepartureParser(Reader reader, BusStop stop) {
        this.reader = reader;
        this.stop = stop;
    }

    /**
     * Get all buses that are on the route passed in
     * @return buses on the route
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List<Departure> parse() throws XmlPullParserException, IOException {
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xpp.setInput(reader);
        xpp.nextTag();
        return readFeed(xpp);
    }

    private List<Departure> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Departure> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, "DocumentElement");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(NEXT_DEPARTURES)) {

                Departure d = readDeparture(parser);

                if (d != null)
                    entries.add(d);

            } else {
                XmlLoader.skip(parser);
            }
        }
        return entries;
    }

    private Departure readDeparture(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, NEXT_DEPARTURES);
        String time = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();

            if (name.equals(TIME)) {
                time = readString(parser, TIME);
            } else {
                XmlLoader.skip(parser);
            }
        }

        Departure d = null;
        try {
            d = new Departure(stop, time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d;
    }

    private static String readString(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, tag);
        String s = XmlLoader.readText(parser);
        parser.require(XmlPullParser.END_TAG, XmlLoader.NAMESPACE, tag);
        return s;
    }
}
