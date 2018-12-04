package uk.me.desiderio.shiftt.data.database.model;

import com.twitter.sdk.android.core.models.Place.BoundingBox;

import java.util.List;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import uk.me.desiderio.shiftt.data.database.converter.BoundingBoxCoordinatesTypeConverter;

/**
 * Room entity class for the {@link BoundingBox} Twitter data object
 */
@Entity
public class BoundingBoxEnt {

    @PrimaryKey
    public long bbid;
    public final String type;

    @TypeConverters(BoundingBoxCoordinatesTypeConverter.class)
    public final List<List<List<Double>>> coordinates;

    public BoundingBoxEnt(long bbid, List<List<List<Double>>> coordinates, String type) {
        this.bbid = bbid;
        this.coordinates = coordinates;
        this.type = type;
    }

    BoundingBoxEnt(BoundingBox boundingBox) {
        type = boundingBox.type;
        coordinates = boundingBox.coordinates;
    }

    /**
     * Returns a {@link BoundingBox} Twitter data object
     */
    public BoundingBox getSeed() {
        return new BoundingBox(coordinates, type);
    }
}
