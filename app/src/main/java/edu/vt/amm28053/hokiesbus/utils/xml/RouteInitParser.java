package edu.vt.amm28053.hokiesbus.utils.xml;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import edu.vt.amm28053.hokiesbus.transit.BusRoute;

/**
 * Created by alex on 8/12/15.
 */
public class RouteInitParser {
    private final Reader reader;

    private static final String SHORT_NAME = "RouteShortName";
    private static final String LONG_NAME = "RouteName";
    private static final String CURRENT_ROUTES = "CurrentRoutes";


    public RouteInitParser(Reader reader) {
        this.reader = reader;
    }

    public List<BusRoute> parse() throws XmlPullParserException, IOException {
        XmlPullParser xpp = Xml.newPullParser();
        xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        xpp.setInput(reader);
        xpp.nextTag();
        return readFeed(xpp);
    }

    private static List<BusRoute> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<BusRoute> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, "DocumentElement");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(CURRENT_ROUTES)) {
                entries.add(readRoute(parser));
            } else {
                XmlLoader.skip(parser);
            }
        }
        return entries;
    }

    private static BusRoute readRoute(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, CURRENT_ROUTES);
        String shortName = null;
        String longName = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(LONG_NAME)) {
                longName = readLongName(parser);
            } else if (name.equals(SHORT_NAME)) {
                shortName = readShortName(parser);
            } else {
                XmlLoader.skip(parser);
            }
        }
        return new BusRoute(shortName, longName);
    }

    private static String readLongName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, LONG_NAME);
        String longName = XmlLoader.readText(parser);
        parser.require(XmlPullParser.END_TAG, XmlLoader.NAMESPACE, LONG_NAME);
        return longName;
    }

    private static String readShortName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, XmlLoader.NAMESPACE, SHORT_NAME);
        String shortName = XmlLoader.readText(parser);
        parser.require(XmlPullParser.END_TAG, XmlLoader.NAMESPACE, SHORT_NAME);
        return shortName;
    }

}
