package uk.me.desiderio.shiftt.data.database.model;

import com.twitter.sdk.android.core.models.HashtagEntity;
import com.twitter.sdk.android.core.models.MediaEntity;
import com.twitter.sdk.android.core.models.MentionEntity;
import com.twitter.sdk.android.core.models.SymbolEntity;
import com.twitter.sdk.android.core.models.TweetEntities;
import com.twitter.sdk.android.core.models.UrlEntity;

import java.util.ArrayList;
import java.util.List;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import uk.me.desiderio.shiftt.data.database.converter.MediaEntityTypeConverter;
import uk.me.desiderio.shiftt.data.database.converter.MentionEntityTypeConverter;
import uk.me.desiderio.shiftt.data.database.converter.SymbolEntityTypeConverter;
import uk.me.desiderio.shiftt.data.database.converter.UrlEntityTypeConverter;


/**
 * Room entity class for the {@link TweetEntities} Twitter data object
 */
@Entity(tableName = "entities")
public class TweetEntitiesEnt implements SeedProvider<TweetEntities> {

    @TypeConverters(UrlEntityTypeConverter.class)
    public final List<UrlEntity> urls;

    @TypeConverters(MentionEntityTypeConverter.class)
    @ColumnInfo(name = "user_mentions")
    public final List<MentionEntity> userMentions;

    @TypeConverters(MediaEntityTypeConverter.class)
    public final List<MediaEntity> media;
    @TypeConverters(SymbolEntityTypeConverter.class)
    public final List<SymbolEntity> symbols;
    @Ignore
    public final List<HashtagEntityEnt> hashtags;
    @PrimaryKey(autoGenerate = true)
    public long id;

    public TweetEntitiesEnt(List<UrlEntity> urls,
                            List<MentionEntity> userMentions,
                            List<MediaEntity> media,
                            List<SymbolEntity> symbols) {
        this.urls = urls;
        this.userMentions = userMentions;
        this.media = media;
        this.hashtags = new ArrayList<>();
        this.symbols = symbols;
    }

    @Ignore
    public TweetEntitiesEnt(TweetEntities tweetEntities) {
        this.urls = tweetEntities.urls;
        this.userMentions = tweetEntities.userMentions;
        this.media = tweetEntities.media;
        this.hashtags = getHashtagEntities(tweetEntities.hashtags);
        this.symbols = tweetEntities.symbols;
    }

    /**
     * Returns a {@link TweetEntities} Twitter data object
     */
    public TweetEntities getSeed() {
        return new TweetEntities(urls,
                                 userMentions,
                                 media,
                                 getRawHashtagList(hashtags),
                                 symbols);
    }

    private List<HashtagEntityEnt> getHashtagEntities(List<HashtagEntity> hashtags) {
        List<HashtagEntityEnt> hashtagEntitiesList = new ArrayList<>();
        for (HashtagEntity hashtag : hashtags) {
            hashtagEntitiesList.add(new HashtagEntityEnt(hashtag));
        }
        return hashtagEntitiesList;
    }

    private List<HashtagEntity> getRawHashtagList(List<HashtagEntityEnt> hashtagEntities) {
        List<HashtagEntity> hashtagList = new ArrayList<>();
        for (HashtagEntityEnt hashtag : hashtagEntities) {
            hashtagList.add(hashtag.getSeed());
        }
        return hashtagList;
    }
}
