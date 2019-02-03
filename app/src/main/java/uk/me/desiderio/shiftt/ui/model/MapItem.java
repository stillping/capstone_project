package uk.me.desiderio.shiftt.ui.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import uk.me.desiderio.shiftt.ui.map.ShifttMapFragment;

/**
 * View data object to be shown at the {@link ShifttMapFragment}
 */
public class MapItem {

    public final String name;
    public final List<LatLng> coordinates;

    public MapItem(String name, List<LatLng> coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }
}
