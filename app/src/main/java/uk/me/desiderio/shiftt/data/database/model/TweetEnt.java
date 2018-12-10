package uk.me.desiderio.shiftt.data.database.model;

import com.twitter.sdk.android.core.models.Card;
import com.twitter.sdk.android.core.models.Coordinates;
import com.twitter.sdk.android.core.models.Place;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.User;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import uk.me.desiderio.shiftt.data.database.converter.CardTypeConverter;
import uk.me.desiderio.shiftt.data.database.converter.CurrentUserRetweetDataTypeConverter;
import uk.me.desiderio.shiftt.data.database.converter.IntegerListTypeConverter;
import uk.me.desiderio.shiftt.data.database.converter.ObjectTypeConverter;
import uk.me.desiderio.shiftt.data.database.converter.StringListTypeConverter;

/**
 * Room entity class for the {@link Tweet} Twitter data object
 */
@Entity(tableName = "tweet")
public class TweetEnt implements SeedProvider {

    @NonNull
    @ColumnInfo(name = "created_at")
    public final String createdAt;
    @ColumnInfo(name = "favorite_count")
    public final Integer favoriteCount;
    public final boolean favorited;
    @ColumnInfo(name = "filter_level")
    public final String filterLevel;
    @PrimaryKey
    public final long id;
    @ColumnInfo(name = "id_str")
    public final String idStr;
    @ColumnInfo(name = "in_reply_to_screen_name")
    public final String inReplyToScreenName;
    @ColumnInfo(name = "in_reply_to_status_id")
    public final long inReplyToStatusId;
    @ColumnInfo(name = "in_reply_to_status_id_str")
    public final String inReplyToStatusIdStr;
    @ColumnInfo(name = "in_reply_to_user_id")
    public final long inReplyToUserId;
    @ColumnInfo(name = "in_reply_to_user_id_str")
    public final String inReplyToUserIdStr;
    public final String lang;
    @ColumnInfo(name = "possibly_sensitive")
    public final boolean possiblySensitive;
    @ColumnInfo(name = "quoted_status_id")
    public final long quotedStatusId;
    @ColumnInfo(name = "quoted_status_id_str")
    public final String quotedStatusIdStr;
    @ColumnInfo(name = "retweet_count")
    public final int retweetCount;
    @ColumnInfo(name = "retweeted")
    public final boolean retweeted;
    public final String source;
    public final String text;
    public final boolean truncated;
    @ColumnInfo(name = "withheld_copyright")
    public final boolean withheldCopyright;
    @ColumnInfo(name = "withheld_scope")
    public final String withheldScope;
    public boolean isMetadata = false;
    @Ignore
    public CoordinatesEnt coordinates;
    @ColumnInfo(name = "coordinates_id")
    public long coordinatesId;
    @ColumnInfo(name = "current_user_retweet")
    @TypeConverters(CurrentUserRetweetDataTypeConverter.class)
    public CurrentUserRetweetEnt currentUserRetweet;
    @Ignore
    public TweetEntitiesEnt entities;
    @ColumnInfo(name = "entities_id")
    public long entitiesId;
    @Ignore
    public TweetEntitiesEnt extendedEntities;
    @ColumnInfo(name = "extended_entities_id")
    public long extendedEntitiesId;
    @Ignore
    public PlaceEnt place;
    @ColumnInfo(name = "place_id")
    public String placeId;
    // this is not implemented //
    @Ignore
    @TypeConverters(ObjectTypeConverter.class)
    public Object scopes;
    @Ignore
    public Tweet quotedStatus;
    @Ignore
    public Tweet retweetedStatus;
    @ColumnInfo(name = "retweeted_status_id")
    public long retweetedStatusId;
    @TypeConverters(IntegerListTypeConverter.class)
    @ColumnInfo(name = "display_text_range")
    public List<Integer> displayTextRange;
    @Ignore
    public UserEnt user;
    @ColumnInfo(name = "user_id")
    public long userId;
    @TypeConverters(StringListTypeConverter.class)
    @ColumnInfo(name = "withheld_in_countries")
    public List<String> withheldInCountries;
    @TypeConverters(CardTypeConverter.class)
    public Card card;

    public TweetEnt(long coordinatesId, String createdAt, Integer favoriteCount,
                    boolean favorited, String filterLevel, long id, String idStr,
                    String inReplyToScreenName, long inReplyToStatusId, String inReplyToStatusIdStr,
                    long inReplyToUserId, String inReplyToUserIdStr, String lang,
                    String placeId,
                    boolean possiblySensitive, long quotedStatusId, String quotedStatusIdStr,
                    int retweetCount, boolean retweeted,
                    long retweetedStatusId,
                    String source, String text, boolean truncated, long userId,
                    boolean withheldCopyright,
                    String withheldScope) {
        this.coordinatesId = coordinatesId;
        this.createdAt = createdAt;
        this.favoriteCount = favoriteCount;
        this.favorited = favorited;
        this.filterLevel = filterLevel;
        this.id = id;
        this.idStr = idStr;
        this.inReplyToScreenName = inReplyToScreenName;
        this.inReplyToStatusId = inReplyToStatusId;
        this.inReplyToStatusIdStr = inReplyToStatusIdStr;
        this.inReplyToUserId = inReplyToUserId;
        this.inReplyToUserIdStr = inReplyToUserIdStr;
        this.lang = lang;
        this.placeId = placeId;
        this.possiblySensitive = possiblySensitive;
        this.quotedStatusId = quotedStatusId;
        this.quotedStatusIdStr = quotedStatusIdStr;
        this.retweetCount = retweetCount;
        this.retweeted = retweeted;
        this.retweetedStatusId = retweetedStatusId;
        this.source = source;
        this.text = text;
        this.truncated = truncated;
        this.userId = userId;
        this.withheldCopyright = withheldCopyright;
        this.withheldScope = withheldScope;
    }

    @Ignore
    public TweetEnt(Tweet tweet) {
        if (tweet.coordinates != null) {
            this.coordinates = new CoordinatesEnt(tweet.coordinates);
        }
        this.createdAt = tweet.createdAt;

        if (tweet.currentUserRetweet != null) {
            //Log.d("TweetEnt", "TweetEnt: currentUserRetweet: " + tweet.currentUserRetweet);
            CurrentUserRetweetEnt currentUserRetweet =
                    getCurrentUserRetweet(tweet.currentUserRetweet);
            if (currentUserRetweet != null) {
                this.currentUserRetweet = currentUserRetweet;
            }
        }

        // the tweet object will generate a entities object with empty lists
        this.entities = new TweetEntitiesEnt(tweet.entities);
        this.extendedEntities = new TweetEntitiesEnt(tweet.extendedEntities);

        this.favoriteCount = tweet.favoriteCount;
        this.favorited = tweet.favorited;
        this.filterLevel = tweet.filterLevel;
        this.id = tweet.id;
        this.idStr = tweet.idStr;
        this.inReplyToScreenName = tweet.inReplyToScreenName;
        this.inReplyToStatusId = tweet.inReplyToStatusId;
        this.inReplyToStatusIdStr = tweet.inReplyToStatusIdStr;
        this.inReplyToUserId = tweet.inReplyToUserId;
        this.inReplyToUserIdStr = tweet.inReplyToUserIdStr;
        this.lang = tweet.lang;
        if (tweet.place != null) {
            this.place = new PlaceEnt(tweet.place);
        }
        this.possiblySensitive = tweet.possiblySensitive;
        this.scopes = tweet.scopes;
        this.quotedStatusId = tweet.quotedStatusId;
        this.quotedStatusIdStr = tweet.quotedStatusIdStr;
        this.quotedStatus = tweet.quotedStatus;
        this.retweetCount = tweet.retweetCount;
        this.retweeted = tweet.retweeted;
        this.retweetedStatus = tweet.retweetedStatus;
        this.source = tweet.source;
        this.text = tweet.text;
        this.displayTextRange = tweet.displayTextRange;
        this.truncated = tweet.truncated;
        if (tweet.user != null) {
            this.user = new UserEnt(tweet.user);
        }
        this.withheldCopyright = tweet.withheldCopyright;
        this.withheldInCountries = tweet.withheldInCountries;
        this.withheldScope = tweet.withheldScope;
        this.card = tweet.card;
    }

    /**
     * Returns a {@link Tweet} Twitter data object
     */
    @SuppressWarnings("unchecked")
    public Tweet getSeed() {
        Coordinates coor = getChildSeed(coordinates);
        TweetEntities tweetEntities = getChildSeed(entities);
        TweetEntities tweetExtendedEntities = getChildSeed(extendedEntities);
        Place tweetPlace = getChildSeed(place);
        User tweetUser = getChildSeed(user);

        return new Tweet(coor, createdAt, currentUserRetweet, tweetEntities,
                         tweetExtendedEntities, favoriteCount, favorited, filterLevel, id, idStr,
                         inReplyToScreenName, inReplyToStatusId, inReplyToStatusIdStr,
                         inReplyToUserId, inReplyToUserIdStr, lang, tweetPlace,
                         possiblySensitive, scopes, quotedStatusId, quotedStatusIdStr,
                         quotedStatus, retweetCount, retweeted, retweetedStatus, source,
                         text, displayTextRange, truncated, tweetUser, withheldCopyright,
                         withheldInCountries, withheldScope, card);
    }

    /**
     * helper method to return the original Twitter data object from the Room entity
     */
    @SuppressWarnings("unchecked")
    private <T> T getChildSeed(SeedProvider seedProvider) {
        String methodName = "getSeed";

        if (seedProvider == null) {
            return null;
        }

        try {
            Method method = seedProvider.getClass().getMethod(methodName);
            return (T) method.invoke(seedProvider);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * helper method to convert the {@link Tweet} 'current user retweet' plain object
     * into a {@link CurrentUserRetweetEnt}
     */
    public CurrentUserRetweetEnt getCurrentUserRetweet(Object tweetCurrentUserRetweetObject) {
        Field idField = getField(tweetCurrentUserRetweetObject, "id");
        if (idField != null) {
            idField.setAccessible(true);
        }
        Field idStrField = getField(tweetCurrentUserRetweetObject, "id_str");
        if (idStrField != null) {
            idStrField.setAccessible(true);
        }

        try {
            Long id = idField != null ? idField.getLong(tweetCurrentUserRetweetObject) : 0;
            String idStr = (String) idStrField.get(tweetCurrentUserRetweetObject);
            return new CurrentUserRetweetEnt(id, idStr);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Field getField(Object object, String fieldName) {
        try {
            return object.getClass().getField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
}
