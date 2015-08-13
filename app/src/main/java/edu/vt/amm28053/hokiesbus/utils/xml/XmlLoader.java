package edu.vt.amm28053.hokiesbus.utils.xml;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import edu.vt.amm28053.hokiesbus.transit.Bus;
import edu.vt.amm28053.hokiesbus.transit.BusPattern;
import edu.vt.amm28053.hokiesbus.transit.BusRoute;

/**
 * Created by alex on 8/11/15.
 */
public class XmlLoader {

    static final String NAMESPACE = null;

    static final String CURRENT_ROUTES_URL = "http://www.bt4u.org/webservices/bt4u_webservice.asmx/GetCurrentRoutes";
    static final String CURRENT_BUS_INFO_URL = "http://www.bt4u.org/webservices/bt4u_webservice.asmx/GetCurrentBusInfo";
    static final String PATTERN_POINTS_URL = "http://www.bt4u.org//webservices/bt4u_webservice.asmx/GetScheduledPatternPoints";

    public static List<BusRoute> getCurrentBusRoutes() throws XmlPullParserException, IOException {
        Reader reader = getXmlReader(CURRENT_ROUTES_URL);
        List<BusRoute> routes = new RouteInitParser(reader).parse();
        reader.close();

        return routes;
    }

    public static List<Bus> getBusesOnRoute(String routeShortName) throws XmlPullParserException, IOException {

        Reader reader = getXmlReader(CURRENT_BUS_INFO_URL);
        List<Bus> buses = new BusParser(reader).parse(routeShortName);
        reader.close();

        return buses;
    }

    public static void loadPatternPoints(Bus b) throws XmlPullParserException, IOException {

        StringBuilder query = new StringBuilder(PATTERN_POINTS_URL);
        query.append("?patternName=");
        query.append(URLEncoder.encode(b.getPatternName(), "UTF-8"));

        Reader reader = getXmlReader(query.toString());
        List<BusPattern.PatternPoint> points =
                new PatternPointParser(reader, b.getPatternName()).parse();

        b.clearPatternPoints();

        for (BusPattern.PatternPoint p : points) {
            b.addPatternPoint(p);
        }
        reader.close();
    }

    static Reader getXmlReader(String urlStr) {
        URL url;
        HttpURLConnection conn;
        BufferedReader rd = null;

        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rd;
    }

    static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    // For the tags title and summary, extracts their text values.
    static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
