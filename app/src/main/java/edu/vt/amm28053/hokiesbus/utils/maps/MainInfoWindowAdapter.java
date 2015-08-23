package edu.vt.amm28053.hokiesbus.utils.xml.maps;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Map;

/**
 * Created by alex on 8/13/15.
 */
public class MainInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    Map<String, GoogleMap.InfoWindowAdapter> adapterMap;

    public MainInfoWindowAdapter(
            Map<String, GoogleMap.InfoWindowAdapter> adapterMap) {
        this.adapterMap = adapterMap;
    }

    @Override
    public View getInfoContents(Marker marker) {
        GoogleMap.InfoWindowAdapter adapter = adapterMap.get(marker.getId());
        return adapter.getInfoContents(marker);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        GoogleMap.InfoWindowAdapter adapter = adapterMap.get(marker.getId());
        return adapter.getInfoWindow(marker);
    }

}
