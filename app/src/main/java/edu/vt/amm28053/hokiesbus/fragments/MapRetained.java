package edu.vt.amm28053.hokiesbus.fragments;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.vt.amm28053.hokiesbus.MainActivity;
import edu.vt.amm28053.hokiesbus.R;
import edu.vt.amm28053.hokiesbus.transit.Bus;
import edu.vt.amm28053.hokiesbus.transit.BusRoute;
import edu.vt.amm28053.hokiesbus.utils.xml.XmlLoader;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapRetained extends Fragment {

    private MapRetainedListener activity;

    // The currently running routes
    private List<BusRoute> routes = new ArrayList<>(20);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MapRetainedListener)activity;
        new LoadRoutesAsync().execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.activity = null;
    }

    public void loadFullRoute(BusRoute route) {
        Log.d("HokieBus", "MapRetained began loading of route: " + route.getLongName());
        new LoadFullRouteAsync().execute(route);
    }

    private class LoadFullRouteAsync extends  AsyncTask<BusRoute, Void, BusRoute> {

        @Override
        protected BusRoute doInBackground(BusRoute... params) {
            BusRoute br = params[0];

            String routeShortName = br.getShortName();
            String routeLongName = br.getLongName();

            List<Bus> busesOnRoute = new ArrayList<>();

            try {
                busesOnRoute = XmlLoader.getBusesOnRoute(routeShortName);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            br.clearBuses();

            // Now that the buses are loaded, load all of the pattern points for
            // the bus's pattern.

            for (Bus b: busesOnRoute) {
                try {
                    XmlLoader.loadPatternPoints(b);
                    br.addBus(b);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return br;
        }

        @Override
        protected void onPostExecute(BusRoute busRoute) {
            activity.onLoadRouteFull(busRoute);
        }
    }

    private class LoadRoutesAsync extends AsyncTask<Void, Void, List<BusRoute>> {

        @Override
        protected List<BusRoute> doInBackground(Void... params) {
            List<BusRoute> routes = new ArrayList<>();

            try {
                routes = XmlLoader.getCurrentBusRoutes();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<BusRoute> s) {
            routes.clear();
            routes.addAll(s);
            activity.onLoadRoutesInit(routes);
        }
    }

    public interface MapRetainedListener {
        void onLoadRoutesInit(List<BusRoute> routes);
        void onLoadRouteFull(BusRoute route);
    }
}
