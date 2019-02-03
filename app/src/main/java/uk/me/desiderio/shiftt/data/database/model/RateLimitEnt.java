package uk.me.desiderio.shiftt.data.database.model;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import uk.me.desiderio.shiftt.data.database.converter.DoubleListTypeConverter;
import uk.me.desiderio.shiftt.data.network.model.RateLimit;

/**
 * Room entity class to store data from the {@link RateLimit}  data object
 */
@Entity(tableName = "rate_limit")
public class RateLimitEnt {

    public final long time;
    @NonNull
    @PrimaryKey
    public final String name;
    @TypeConverters(DoubleListTypeConverter.class)
    public final List<Double> coors;
    public final int limit;
    public final long reset;
    public final int remaining;


    public RateLimitEnt(@NonNull String name,
                        List<Double> coors,
                        long time,
                        int limit,
                        int remaining,
                        long reset) {
        this.name = name;
        this.coors = coors;
        this.time = time;
        this.limit = limit;
        this.remaining = remaining;
        this.reset = reset;
    }
}
