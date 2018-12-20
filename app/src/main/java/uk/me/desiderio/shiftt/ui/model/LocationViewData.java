package uk.me.desiderio.shiftt.ui.model;

import uk.me.desiderio.shiftt.ui.neighbourhood.ShifttMapFragment;

/**
 * Data class to provide location to  map views {@link ShifttMapFragment}
 */
public class LocationViewData {
    private double latitude;
    private double longitude;
    private boolean isFresh;

    public LocationViewData(double latitude, double longitude, boolean isFresh) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.isFresh = isFresh;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // TODO implement
    public boolean isFresh() {
        return isFresh;
    }
}
