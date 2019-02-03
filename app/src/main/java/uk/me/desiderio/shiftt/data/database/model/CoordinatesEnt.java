package uk.me.desiderio.shiftt.data.database.model;

import com.twitter.sdk.android.core.models.Coordinates;

import java.util.List;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import uk.me.desiderio.shiftt.data.database.converter.DoubleListTypeConverter;

/**
 * Room entity class for the {@link Coordinates} Twitter data object
 */
@Entity(tableName = "coordinates")
public class CoordinatesEnt implements SeedProvider<Coordinates> {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @TypeConverters(DoubleListTypeConverter.class)
    public List<Double> coordinates;
    public String type;

    public CoordinatesEnt(int id, List<Double> coordinates, String type) {
        this.id = id;
        this.coordinates = coordinates;
        this.type = type;
    }

    @Ignore
    public CoordinatesEnt(Coordinates coordinates) {
        if (coordinates != null) {
            this.coordinates = coordinates.coordinates;
            this.type = coordinates.type;
        }
    }

    /**
     * Returns a {@link Coordinates} Twitter data object
     */
    public Coordinates getSeed() {
        return new Coordinates(coordinates.get(0), coordinates.get(1), type);
    }

}
