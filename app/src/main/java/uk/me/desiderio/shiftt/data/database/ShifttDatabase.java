package uk.me.desiderio.shiftt.data.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import uk.me.desiderio.shiftt.data.database.model.BoundingBoxEnt;
import uk.me.desiderio.shiftt.data.database.model.CoordinatesEnt;
import uk.me.desiderio.shiftt.data.database.model.HashtagEntityEnt;
import uk.me.desiderio.shiftt.data.database.model.PlaceEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEntitiesEnt;
import uk.me.desiderio.shiftt.data.database.model.QueryTweetEntitiesHashtagEntityJoin;
import uk.me.desiderio.shiftt.data.database.model.UserEnt;

@Database(entities = {TweetEnt.class,
        CoordinatesEnt.class,
        PlaceEnt.class,
        BoundingBoxEnt.class,
        TweetEntitiesEnt.class,
        HashtagEntityEnt.class,
        UserEnt.class,
        QueryTweetEntitiesHashtagEntityJoin.class},
        version = 3,
        exportSchema = false)
public abstract class ShifttDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "shiftt";

    public abstract ShifttDao shifttDao();
}
