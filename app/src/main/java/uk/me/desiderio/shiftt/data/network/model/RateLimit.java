package uk.me.desiderio.shiftt.data.network.model;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Holds parameters to be used determining whether to allow a new Twitter API request
 */
public class RateLimit {

    /**
     * name of the request as define in the Twitter rate limit status
     */
    public final String name;

    /**
     * latitude and longitude used as query for the Twitter request
     */
    public final List<Double> coors;

    /**
     * time when request was made last
     */
    public final long time;

    /**
     * max number of request for a 15min window
     * this value is returned as part of the request header
     */
    public final int limit;

    /**
     * remaining number of request in the current 15min window
     * this value is returned as part of the request header
     */
    public final int remaining;

    /**
     * UTC epoch of the time in the future when the 15min window will reset
     * this value is returned as part of the request header
     */
    public final long reset;


    public RateLimit(String name,
                     List<Double> coordinates,
                     long time,
                     int limit,
                     int remaining,
                     long reset) {
        this.name = name;
        this.time = time;
        this.coors = coordinates;
        this.limit = limit;
        this.remaining = remaining;
        this.reset = reset;
    }


    public RateLimit(@NonNull String name,
                     double lat,
                     double lng,
                     long time,
                     @NonNull String limit,
                     @NonNull String remaining,
                     @NonNull String reset) {
        this.name = name;
        this.time = time;
        this.coors = Arrays.asList(lat, lng);
        this.limit = (limit != null)? Integer.parseInt(limit) : 0;
        this.remaining = (remaining != null)? Integer.parseInt(remaining) : 0;
        this.reset = (reset != null)? Long.parseLong(reset) : 0;
    }
}
