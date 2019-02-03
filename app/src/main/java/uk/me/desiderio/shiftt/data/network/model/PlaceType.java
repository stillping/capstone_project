package uk.me.desiderio.shiftt.data.network.model;

import androidx.annotation.VisibleForTesting;
import uk.me.desiderio.shiftt.data.network.TrendsByPlaceIdService;

/**
 * Retrofit network data object for the {@link TrendsByPlaceIdService}  response
 */
@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
public class PlaceType {
    public final int code;
    public final String name;

    @VisibleForTesting
    public PlaceType(int code, String name) {
        this.code = code;
        this.name = name;
    }
}
