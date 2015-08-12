package edu.vt.amm28053.hokiesbus.transit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.vt.amm28053.hokiesbus.R;
import edu.vt.amm28053.hokiesbus.transit.BusRoute;

/**
 * Created by alex on 8/12/15.
 */
public class RouteAdapter extends ArrayAdapter<BusRoute> {

    public RouteAdapter(Context context,List<BusRoute> routes) {
        super(context, 0,routes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BusRoute route = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_route, parent, false);
        }

        TextView shortName = (TextView)convertView.findViewById(R.id.shortName);
        TextView longName = (TextView)convertView.findViewById(R.id.longName);

        shortName.setText(route.getShortName());
        longName.setText(route.getLongName());

        return convertView;
    }


}
