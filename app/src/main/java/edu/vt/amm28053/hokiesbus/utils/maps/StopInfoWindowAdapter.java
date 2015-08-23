package edu.vt.amm28053.hokiesbus.utils.xml.maps;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.w3c.dom.Text;

import edu.vt.amm28053.hokiesbus.R;

/**
 * Created by alex on 8/13/15.
 */
public class StopInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater;

    public StopInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = inflater.inflate(R.layout.stop_info_layout, null);

        String[] args = marker.getSnippet().split("\n");


        TextView title = (TextView)v.findViewById(R.id.title_stop);
        title.setText(marker.getTitle());

        // Bus
        TextView bus = (TextView)v.findViewById(R.id.bus_name);

        // Next 3 stops
        TextView stop1 = (TextView)v.findViewById(R.id.stop_1);
        TextView stop2 = (TextView)v.findViewById(R.id.stop_2);
        TextView stop3 = (TextView)v.findViewById(R.id.stop_3);

        bus.setText(args[0]);

        if (args.length >= 4) {
            stop1.setText(args[1]);
            stop2.setText(args[2]);
            stop3.setText(args[3]);
        }

        return v;
    }
}
