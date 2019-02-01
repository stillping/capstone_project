package uk.me.desiderio.shiftt.data.database;

import android.util.Log;

import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;
import java.util.stream.Collectors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import uk.me.desiderio.shiftt.data.database.model.CoordinatesEnt;
import uk.me.desiderio.shiftt.data.database.model.HashtagEntityEnt;
import uk.me.desiderio.shiftt.data.database.model.PlaceEnt;
import uk.me.desiderio.shiftt.data.database.model.QueryTweetEnt;
import uk.me.desiderio.shiftt.data.database.model.QueryTweetEntitiesHashtagEntityJoin;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEntitiesEnt;
import uk.me.desiderio.shiftt.data.database.model.UserEnt;

@Dao
public abstract class TweetsDao {

    private static final String TAG = TweetsDao.class.getSimpleName();

    // QUERY

    public LiveData<List<Tweet>> getAllFeaturedPopTweets() {
        LiveData<List<QueryTweetEnt>> queryLiveData = getAllFeaturedPopTweetsEntQuery();
        return Transformations.map(queryLiveData,
                                   queryTweetEntList -> queryTweetEntList.stream()
                                           .map(queryTweetEnt -> queryTweetEnt
                                                   .getPopulatedTweetEnt().getSeed())
                                           .collect(Collectors.toList()));
    }

    public LiveData<Tweet> getFeaturedPopTweetById(long id) {
        LiveData<QueryTweetEnt> tweetEntLiveData = getFeaturedPopTweetEntQuery(id);
        return Transformations.map(tweetEntLiveData,
                                   tweetEnt -> tweetEnt.getPopulatedTweetEnt().getSeed());
    }

    @Transaction
    @Query("SELECT * FROM tweet WHERE NOT isMetadata")
    abstract LiveData<List<QueryTweetEnt>> getAllFeaturedPopTweetsEntQuery();

    @Transaction
    @Query("SELECT * FROM tweet " +
            "INNER JOIN place ON tweet.place_id = place.id " +
            "WHERE place.full_name = :fullName")
    public abstract LiveData<List<QueryTweetEnt>> getAllFeaturedPopTweetsEntQueryOnPlace(String fullName);

    @Transaction
    @Query("SELECT * FROM tweet WHERE NOT isMetadata AND id = :id")
    abstract LiveData<QueryTweetEnt> getFeaturedPopTweetEntQuery(long id);

    @Query("SELECT DISTINCT place.* FROM place " +
            "INNER JOIN tweet on tweet.place_id = place.id " +
            "WHERE NOT isMetadata")
    public abstract LiveData<List<PlaceEnt>> getAllPlaces();

    @Query("SELECT * FROM coordinates WHERE id = :id")
    public abstract CoordinatesEnt getCoordinates(long id);

    @Query("SELECT * FROM place WHERE id = :id")
    public abstract PlaceEnt getPlace(String id);

    @Query("SELECT * FROM entities WHERE id = :id")
    public abstract TweetEntitiesEnt getRawTweetEntities(long id);

    @Query("SELECT * FROM hashtag WHERE text = :text")
    public abstract HashtagEntityEnt getHashtagEntity(String text);

    @Query("SELECT * FROM entities_hastags_join WHERE tweet_entities_id IN (:entitiesId)")
    public abstract List<QueryTweetEntitiesHashtagEntityJoin> getHashtagEntityListByEntitiesId(
            long entitiesId);

    // INSERT

    public void insertTweetEntities(List<TweetEnt> tweetEntities) {
        for (TweetEnt tweetEntity : tweetEntities) {
            insertTweetData(tweetEntity);
        }
    }

    private void insertTweetData(TweetEnt tweetEntity) {
        Long coorId = -1L;
        Log.d(TAG, "insertTweetEntities: ID: " + tweetEntity.id);
        if (tweetEntity.coordinates != null) {
            coorId = insertCoordinates(tweetEntity.coordinates);
        }
        tweetEntity.coordinatesId = coorId;


        if (tweetEntity.place != null) {
            insertPlace(tweetEntity.place);
            tweetEntity.placeId = tweetEntity.place.id;
        }

        if (hasAnyEntity(tweetEntity.entities)) {
            // insert hasttags into table and record keys
            tweetEntity.entitiesId = insertRawEntities(tweetEntity.entities);
            if (hasElements(tweetEntity.entities.hashtags)) {
                for (HashtagEntityEnt hashtag : tweetEntity.entities.hashtags) {
                    insertHashtag(hashtag);
                    insertEntitiesHashtagJoin(
                            new QueryTweetEntitiesHashtagEntityJoin(tweetEntity.entitiesId,
                                                                    hashtag.text));
                }

            }
        }

        if (hasAnyEntity(tweetEntity.extendedEntities)) {
            // insert hasttags into table and record keys
            tweetEntity.extendedEntitiesId = insertRawEntities(tweetEntity.extendedEntities);
            if (hasElements(tweetEntity.extendedEntities.hashtags)) {
                for (HashtagEntityEnt hashtag : tweetEntity.extendedEntities.hashtags) {
                    Log.d(TAG, tweetEntity.id + ": insert+TweetData: id: " + tweetEntity
                            .entitiesId + " : hashtag: "
                            + hashtag.text);
                    insertHashtag(hashtag);
                    insertEntitiesHashtagJoin(
                            new QueryTweetEntitiesHashtagEntityJoin(tweetEntity.extendedEntitiesId,
                                                                    hashtag.text));
                }

            }
        }

        if (tweetEntity.quotedStatus != null) {
            TweetEnt quotedStatusData = new TweetEnt(tweetEntity.quotedStatus);
            quotedStatusData.isMetadata = true;
            insertTweetData(quotedStatusData);
        }

        if (tweetEntity.retweetedStatus != null) {
            tweetEntity.retweetedStatusId = tweetEntity.retweetedStatus.id;
            TweetEnt retweetedStatusData = new TweetEnt(tweetEntity.retweetedStatus);
            retweetedStatusData.isMetadata = true;
            insertTweetData(retweetedStatusData);
        }

        if (tweetEntity.user != null) {
            if (tweetEntity.user.status != null) {
                tweetEntity.user.statusId = tweetEntity.user.status.id;

                TweetEnt userStatus = tweetEntity.user.status;
                userStatus.isMetadata = true;
                insertTweetData(userStatus);
            }
            tweetEntity.userId = tweetEntity.user.id;
            insertUser(tweetEntity.user);
        }

        insertTweetEntity(tweetEntity);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertTweetEntity(TweetEnt tweet);

    @Insert
    abstract long insertCoordinates(CoordinatesEnt coordinatesEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insertPlace(PlaceEnt placeEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract long insertRawEntities(TweetEntitiesEnt entities);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insertHashtag(HashtagEntityEnt hashtagEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insertUser(UserEnt user);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract void insertEntitiesHashtagJoin(QueryTweetEntitiesHashtagEntityJoin join);

    // UTILS

    private boolean hasAnyEntity(TweetEntitiesEnt entities) {
        return (hasElements(entities.hashtags) ||
                hasElements(entities.media) ||
                hasElements(entities.symbols) ||
                hasElements(entities.urls) ||
                hasElements(entities.userMentions));
    }

    private boolean hasElements(List<?> list) {
        return list != null && list.size() > 0;
    }
}