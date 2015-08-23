package edu.vt.amm28053.hokiesbus.utils.xml.maps;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import edu.vt.amm28053.hokiesbus.R;

/**
 * Created by alex on 8/13/15.
 */
public class BusInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;

    public BusInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = inflater.inflate(R.layout.bus_info_layout, null);

        String[] args = marker.getSnippet().split("\n");

        TextView title = (TextView)v.findViewById(R.id.title_bus);
        title.setText(marker.getTitle());

        TextView patternName = (TextView)v.findViewById(R.id.pattern_name);
        TextView lastStop = (TextView)v.findViewById(R.id.last_stop);
        TextView passengers = (TextView)v.findViewById(R.id.passengers);

        patternName.setText(args[0]);

        if (args.length >= 3) {
            lastStop.setText(args[1]);
            passengers.setText(args[2]);
        }

        return v;
    }
}
