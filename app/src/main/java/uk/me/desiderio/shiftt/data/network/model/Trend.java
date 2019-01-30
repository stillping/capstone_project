package uk.me.desiderio.shiftt.data.network.model;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.VisibleForTesting;
import uk.me.desiderio.shiftt.data.network.TrendsByPlaceIdService;

/**
 * Retrofit network data object for the {@link TrendsByPlaceIdService}  response
 */
public class Trend {

    public String name;
    public String url;
    public String query;

    // NOT implemented at this stage
    private int promoted_content;

    @SerializedName(value = "tweet_volume")
    public long tweetVolume;

    // this is injected locally at the dao
    public Place place;

    @VisibleForTesting
    public Trend(String name, String url, String query, long tweetVolume, Place place) {
        this.name = name;
        this.url = url;
        this.query = query;
        this.tweetVolume = tweetVolume;
        this.place = place;
    }
}
