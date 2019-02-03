package uk.me.desiderio.shiftt.data.database.model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import uk.me.desiderio.shiftt.data.network.model.Place;

/**
 * Room entity class for the {@link Place} Network data object
 */
@Entity(tableName = "trend_place",
        indices = {@Index(value = {"placeName"}, unique = true)})
public class TrendPlaceEnt implements SeedProvider<Place> {

    public final String country;
    public final String countryCode;

    @PrimaryKey
    @NonNull
    public final String placeName;

    @ColumnInfo(name = "parent_id")
    public final long parentId;

    @Embedded(prefix = "place_type_")
    public PlaceTypeEnt placeType;

    public final String url;
    public final long woeid;

    public TrendPlaceEnt(String country, String countryCode, @NonNull String placeName,
                         long parentId,
                         PlaceTypeEnt placeType,
                         String url, long woeid) {
        this.country = country;
        this.countryCode = countryCode;
        this.placeName = placeName;
        this.parentId = parentId;
        this.placeType = placeType;
        this.url = url;
        this.woeid = woeid;
    }

    public TrendPlaceEnt(@NonNull uk.me.desiderio.shiftt.data.network.model.Place place) {
        this.country = place.country;
        this.countryCode = place.countryCode;
        this.placeName = place.name;
        this.parentId = place.parentId;
        if (place.placeType != null) {
            this.placeType = new PlaceTypeEnt(place.placeType);
        }
        this.url = place.url;
        this.woeid = place.woeid;
    }

    /**
     * Returns a {@link Place} Twitter data object
     */
    public Place getSeed() {
        return new Place(country,
                         countryCode,
                         placeName,
                         parentId,
                         (placeType != null) ? placeType.getSeed() : null,
                         url,
                         woeid);
    }
}
