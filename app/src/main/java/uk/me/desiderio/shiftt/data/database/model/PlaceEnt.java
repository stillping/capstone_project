package uk.me.desiderio.shiftt.data.database.model;

import com.twitter.sdk.android.core.models.Place;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import uk.me.desiderio.shiftt.data.database.converter.PlaceEntityAttributesTypeConverter;

/**
 * Room entity class for the {@link Place} Twitter data object
 */
@Entity(tableName = "place",
        indices = {@Index(value = {"id"}, unique = true)})
public class PlaceEnt implements SeedProvider<Place> {

    @PrimaryKey(autoGenerate = true)
    public int rowId;

    @TypeConverters(PlaceEntityAttributesTypeConverter.class)
    public final Map<String, String> attributes;

    @Embedded
    public final BoundingBoxEnt boundingBox;

    public final String country;

    @ColumnInfo(name = "country_code")
    public final String countryCode;

    @ColumnInfo(name = "full_name")
    public final String fullName;

    public final String id;

    public final String name;

    @ColumnInfo(name = "place_type")
    public final String placeType;

    @ColumnInfo(name = "url")
    public final String url;


    public PlaceEnt(Map<String, String> attributes,
                    BoundingBoxEnt boundingBox,
                    String country,
                    String countryCode,
                    String fullName,
                    @NonNull String id,
                    String name,
                    String placeType,
                    String url) {
        this.attributes = attributes;
        this.boundingBox = boundingBox;
        this.country = country;
        this.countryCode = countryCode;
        this.fullName = fullName;
        this.id = id;
        this.name = name;
        this.placeType = placeType;
        this.url = url;

    }

    @Ignore
    public PlaceEnt(Place place) {
        this(place.attributes,
             new BoundingBoxEnt(place.boundingBox),
             place.country,
             place.countryCode,
             place.fullName,
             place.id,
             place.name,
             place.placeType,
             place.url);
    }

    /**
     * Returns a {@link Place} Twitter data object
     */
    @SuppressWarnings("unchecked")
    public Place getSeed() {
        return new Place(attributes,
                         boundingBox.getSeed(),
                         country,
                         countryCode,
                         fullName,
                         id,
                         name,
                         placeType,
                         url);
    }
}
