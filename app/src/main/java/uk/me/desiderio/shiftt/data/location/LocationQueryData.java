package uk.me.desiderio.shiftt.data.location;

import androidx.annotation.NonNull;

/**
 * Location data used for the Twitter API requests
 */
public class LocationQueryData {
    public final double lat;
    public final double lng;
    public final long time;
    public final String radiusUnit;
    public final String radiusSize;

    public LocationQueryData(double lat,
                             double lng,
                             long time,
                             @NonNull String radiusUnit,
                             @NonNull String radiusSize) {
        this.lat = lat;
        this.lng = lng;
        this.time = time;
        this.radiusUnit = radiusUnit;
        this.radiusSize = radiusSize;
    }
}
