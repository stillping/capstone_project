package uk.me.desiderio.shiftt.data.database.model;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;
import uk.me.desiderio.shiftt.data.network.model.Place;
import uk.me.desiderio.shiftt.data.network.model.Trend;

/**
 * Room Entity to query {@link Trend} data that hold relationship with {@link Place} network data
 */
public class QueryTrendEnt {
    @Embedded
    public TrendEnt trendEntity;

    @Relation(parentColumn = "place_name",
            entityColumn = "placeName",
            entity = TrendPlaceEnt.class)
    public List<TrendPlaceEnt> placeList;


    @Ignore
    public TrendEnt getPopulatedTweetEnt() {

        if (hasPopulatedList(placeList)) {
            trendEntity.place = placeList.get(0);
        }
        return trendEntity;
    }

    private boolean hasPopulatedList(List list) {
        return list != null && !list.isEmpty();
    }
}
