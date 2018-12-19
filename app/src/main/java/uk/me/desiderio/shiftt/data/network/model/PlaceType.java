package uk.me.desiderio.shiftt.data.network.model;

import androidx.annotation.VisibleForTesting;

/**
 * Retrofit network data object for the 'TrendsByPlaceIdService' response
 */
@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public class PlaceType {
    public int code;
    public String name;

    @VisibleForTesting
    public PlaceType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
