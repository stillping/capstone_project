package uk.me.desiderio.shiftt.ui.model;

import uk.me.desiderio.shiftt.ui.map.ShifttMapFragment;

/**
 * Data class to hold location data for map views at {@link ShifttMapFragment}
 */
public class LocationViewData {
    private final double latitude;
    private final double longitude;
    private final long time;


    public LocationViewData(double latitude, double longitude, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTime() {
        return time;
    }
}
