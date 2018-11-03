package uk.me.desiderio.shiftt.model;

import uk.me.desiderio.shiftt.MainActivity;
import uk.me.desiderio.shiftt.NeighbourhoodActivity;

/**
 * Data class to populate map views {@link MainActivity} and {@link NeighbourhoodActivity}
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

    public boolean isFresh() {
        return isFresh;
    }
}
