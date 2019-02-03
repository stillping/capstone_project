package uk.me.desiderio.shiftt.data.database.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import uk.me.desiderio.shiftt.data.network.model.Trend;

/**
 * Room entity class for the {@link Trend} network data object
 */
@Entity(tableName = "trend")
public class TrendEnt {

    @NonNull
    @PrimaryKey
    public final String name;

    public final String url;
    public final String query;

    @Ignore
    @ColumnInfo(name = "promoted_content")
    public int promotedContent;

    @ColumnInfo(name = "tweet_volume")
    public final long tweetVolume;

    // this is injected at dao
    @Ignore
    public TrendPlaceEnt place;
    @ColumnInfo(name = "place_name")
    public String placeName;

    public TrendEnt(@NonNull String name, String url, String query, long tweetVolume, String placeName) {
        this.name = name;
        this.url = url;
        this.query = query;
        this.tweetVolume = tweetVolume;
        this.placeName = placeName;
    }

    @Ignore
    public TrendEnt(Trend trend) {
        this.name = trend.name;
        this.url = trend.url;
        this.query = trend.query;
        this.tweetVolume = trend.tweetVolume;
        if (trend.place != null) {
            this.place = new TrendPlaceEnt(trend.place);
        }
    }

    @Ignore
    public Trend getSeed() {
        return new Trend(name,
                         url,
                         query,
                         tweetVolume,
                         (place != null) ? place.getSeed() : null);
    }
}
