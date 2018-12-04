package uk.me.desiderio.shiftt.data.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import uk.me.desiderio.shiftt.data.database.model.CoordinatesEnt;
import uk.me.desiderio.shiftt.data.database.model.HashtagEntityEnt;
import uk.me.desiderio.shiftt.data.database.model.PlaceEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEntitiesEnt;
import uk.me.desiderio.shiftt.data.database.model.UserEnt;

@Dao
public abstract class ShifttDao {

    private static final String TAG = ShifttDao.class.getSimpleName();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertTweetEntity(TweetEnt tweet);

    @Insert
    public abstract long insertCoordinates(CoordinatesEnt coordinatesEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertPlace(PlaceEnt placeEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertRawEntities(TweetEntitiesEnt entities);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertHashtag(HashtagEntityEnt hashtagEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insertUser(UserEnt user);

    @Query("SELECT id FROM tweet WHERE NOT isMetadata")
    public abstract List<Long> getAllResponseTweetId();

    @Query("SELECT * FROM tweet WHERE id = :id")
    public abstract TweetEnt getTweetEntity(long id);

    @Query("SELECT * FROM coordinates WHERE id = :id")
    public abstract CoordinatesEnt getCoordinates(long id);

    @Query("SELECT * FROM place WHERE id = :id")
    public abstract PlaceEnt getPlace(String id);

    @Query("SELECT * FROM entities WHERE id = :id")
    public abstract TweetEntitiesEnt getRawTweetEntities(long id);

    @Query("SELECT * FROM hashtag WHERE text = :text")
    public abstract HashtagEntityEnt getHashtagEntity(String text);

    @Query("SELECT * FROM hashtag")
    public abstract HashtagEntityEnt[] getAllHashtagEntities();

    @Query("SELECT * FROM user WHERE id = :id")
    public abstract UserEnt getUserById(long id);

    private void insertTweetData(TweetEnt tweetEntity) {
        Long coorId = -1L;
        Log.d(TAG, "1 sahara insertTweetEntities: PLACE present: " + (tweetEntity.place !=
                null));
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
            if (hasElements(tweetEntity.entities.hashtags)) {
                for (HashtagEntityEnt hashtag : tweetEntity.entities.hashtags) {
                    insertHashtag(hashtag);
                    tweetEntity.entities.hashtagIds.add(hashtag.text);
                }
            }

            tweetEntity.entitiesId = insertRawEntities(tweetEntity.entities);
        }

        if (hasAnyEntity(tweetEntity.extendedEntities)) {
            // insert hasttags into table and record keys
            if (hasElements(tweetEntity.extendedEntities.hashtags)) {
                for (HashtagEntityEnt hashtag : tweetEntity.extendedEntities.hashtags) {
                    insertHashtag(hashtag);
                    tweetEntity.extendedEntities.hashtagIds.add(hashtag.text);
                }
            }

            tweetEntity.extendedEntitiesId = insertRawEntities(tweetEntity.extendedEntities);
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
                TweetEnt userStatus = new TweetEnt(tweetEntity.user.status);
                userStatus.isMetadata = true;
                insertTweetData(userStatus);
            }
            tweetEntity.userId = tweetEntity.user.id;
            insertUser(tweetEntity.user);
        }

        insertTweetEntity(tweetEntity);
    }

    public void insertTweetEntities(TweetEnt... tweetEntities) {

        for (TweetEnt tweetEntity : tweetEntities) {
            insertTweetData(tweetEntity);
        }
    }

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

    public List<TweetEnt> getAllResponseTweets() {
        List<Long> tweetEntityIds = getAllResponseTweetId();

        List<TweetEnt> tweetEntities = new ArrayList<>();
        for (Long tweetId : tweetEntityIds) {
            tweetEntities.add(getTweetEntityById(tweetId));
        }
        return tweetEntities;
    }

    public TweetEnt getTweetEntityById(long id) {
        TweetEnt tweetEntity = getTweetEntity(id);
        tweetEntity.coordinates = getCoordinates(tweetEntity.coordinatesId);
        tweetEntity.place = getPlace(tweetEntity.placeId);
        tweetEntity.entities = getRawTweetEntities(tweetEntity.entitiesId);
        tweetEntity.extendedEntities = getRawTweetEntities(tweetEntity.extendedEntitiesId);

        if (tweetEntity.entities != null) {
            for (String hashtagId : tweetEntity.entities.hashtagIds) {
                tweetEntity.entities.hashtags.add(getHashtagEntity(hashtagId));
            }
        }

        if (tweetEntity.extendedEntities != null) {
            for (String extendedHashtagId : tweetEntity.extendedEntities.hashtagIds) {
                tweetEntity.extendedEntities.hashtags.add(getHashtagEntity(extendedHashtagId));
            }
        }

        if (tweetEntity.quotedStatusId != 0) {
            tweetEntity.quotedStatus = getTweetEntityById(tweetEntity.quotedStatusId).getSeed();
        }

        if (tweetEntity.retweetedStatusId != 0) {
            tweetEntity.retweetedStatus = getTweetEntityById(tweetEntity.retweetedStatusId).getSeed();
        }

        if (tweetEntity.userId != 0) {
            tweetEntity.user = getUserById(tweetEntity.userId);
            if (tweetEntity.user != null && tweetEntity.user.statusId != 0) {
                TweetEnt userStatus = getTweetEntityById(tweetEntity.user.statusId);
                tweetEntity.user.status = userStatus.getSeed();
            }
        }
        return tweetEntity;
    }
}
