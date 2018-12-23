package uk.me.desiderio.shiftt.data.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import uk.me.desiderio.shiftt.data.database.model.RateLimitEnt;

@Dao
public abstract class RateLimitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertRateLimit(RateLimitEnt rateLimitEnt);

    @Query("SELECT * FROM rate_limit")
    public abstract LiveData<List<RateLimitEnt>> getAllRateLimits();
}
