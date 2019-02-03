package uk.me.desiderio.shiftt.data.database.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import static androidx.room.ForeignKey.CASCADE;


/**
 * Room Entity to hold M:N relationship between {@link TweetEntitiesEnt} and the
 * {@link HashtagEntityEnt}
 */
@Entity(
        tableName = "entities_hastags_join",
        primaryKeys = {"tweet_entities_id", "hashtag_text"},
        foreignKeys = {
                @ForeignKey(entity = TweetEntitiesEnt.class,
                        parentColumns = "id",
                        childColumns = "tweet_entities_id",
                        onDelete = CASCADE,
                        deferred = true),
                @ForeignKey(entity = HashtagEntityEnt.class,
                        parentColumns = "text",
                        childColumns = "hashtag_text",
                        onDelete = CASCADE,
                        deferred = true),
        },
        indices = {@Index(value = "tweet_entities_id"),
                @Index(value = "hashtag_text"),}

)
public class QueryTweetEntitiesHashtagEntityJoin {
    @ColumnInfo(name = "tweet_entities_id")
    public final long tweetEntitiesId;
    @NonNull
    @ColumnInfo(name = "hashtag_text")
    public final String hashtagText;

    public QueryTweetEntitiesHashtagEntityJoin(long tweetEntitiesId, @NonNull String hashtagText) {
        this.tweetEntitiesId = tweetEntitiesId;
        this.hashtagText = hashtagText;
    }
}
