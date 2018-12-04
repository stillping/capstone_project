package uk.me.desiderio.shiftt.data.database.model;

import java.util.List;

import androidx.room.Entity;
import androidx.room.TypeConverters;
import uk.me.desiderio.shiftt.data.database.converter.IntegerListTypeConverter;

/**
 * Room entity class for the {@link com.twitter.sdk.android.core.models.Entity} Twitter data object
 */
@Entity
public class EntityEnt {

    @TypeConverters(IntegerListTypeConverter.class)
    public final List<Integer> indices;

    public EntityEnt(List<Integer> indices) {
        this.indices = indices;
    }
}
