package uk.me.desiderio.shiftt.data.network.model;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.VisibleForTesting;
import uk.me.desiderio.shiftt.data.network.TrendsByPlaceIdService;

/**
 * Retrofit network data object for the {@link TrendsByPlaceIdService}  response
 */
public class Place {

    public final String country;
    public final String countryCode;
    public final String name;

    @SerializedName(value = "parentid")
    public final long parentId;

    public final PlaceType placeType;
    public final String url;
    public final long woeid;


    @VisibleForTesting
    public Place(String country, String countryCode, String name, long parentId, PlaceType placeType, String url, long woeid) {
        this.country = country;
        this.countryCode = countryCode;
        this.name = name;
        this.parentId = parentId;
        this.placeType = placeType;
        this.url = url;
        this.woeid = woeid;
    }
}