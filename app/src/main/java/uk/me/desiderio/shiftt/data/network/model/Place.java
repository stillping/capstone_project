package uk.me.desiderio.shiftt.data.network.model;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.VisibleForTesting;

/**
 * Retrofit network data object for the 'TrendsByPlaceIdService' response
 */
public class Place {

    public String country;
    public String countryCode;
    public String name;

    @SerializedName(value = "parentid")
    public long parentId;

    public PlaceType placeType;
    public String url;
    public long woeid;


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
