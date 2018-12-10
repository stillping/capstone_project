package uk.me.desiderio.shiftt.data.database.model;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

/**
 * Room Entity to hold {@link Tweet} data in the {@link TweetEnt} query
 */
public class QueryTweetEnt {
    @Embedded
    public TweetEnt tweetEntity;

    @Relation(parentColumn = "coordinates_id",
            entityColumn = "id",
            entity = CoordinatesEnt.class)
    public List<CoordinatesEnt> coordinatesList;

    @Relation(parentColumn = "place_id",
            entityColumn = "id",
            entity = PlaceEnt.class)
    public List<PlaceEnt> placeList;

    @Relation(parentColumn = "entities_id",
            entityColumn = "id",
            entity = TweetEntitiesEnt.class)
    public List<TweetEntitiesEnt> entitiesList;

    @Relation(parentColumn = "extended_entities_id",
            entityColumn = "id",
            entity = TweetEntitiesEnt.class)
    public List<TweetEntitiesEnt> extendedEntitiesList;

    @Relation(parentColumn = "quoted_status_id",
            entityColumn = "id",
            entity = TweetEnt.class)
    public List<TweetEnt> quotedStatusList;

    @Relation(parentColumn = "retweeted_status_id",
            entityColumn = "id",
            entity = TweetEnt.class)
    public List<TweetEnt> retweetedStatusList;

    @Relation(parentColumn = "user_id",
            entityColumn = "id",
            entity = UserEnt.class)
    public List<QueryUserEnt> userList;

    @Ignore
    public TweetEnt getPopulatedTweetEnt() {
        if (hasPopulatedList(coordinatesList)) {
            tweetEntity.coordinates = coordinatesList.get(0);
        }
        if (hasPopulatedList(placeList)) {
            tweetEntity.place = placeList.get(0);
        }
        if (hasPopulatedList(entitiesList)) {
            tweetEntity.entities = entitiesList.get(0);
        }
        if (hasPopulatedList(extendedEntitiesList)) {
            tweetEntity.extendedEntities = extendedEntitiesList.get(0);
        }
        if (hasPopulatedList(quotedStatusList)) {
            tweetEntity.quotedStatus = quotedStatusList.get(0).getSeed();
        }
        if (hasPopulatedList(retweetedStatusList)) {
            tweetEntity.retweetedStatus = retweetedStatusList.get(0).getSeed();
        }
        if (hasPopulatedList(userList)) {
            tweetEntity.user = userList.get(0).getUserEnt();
        }
        return tweetEntity;
    }

    private boolean hasPopulatedList(List list) {
        return list != null && !list.isEmpty();
    }
}
