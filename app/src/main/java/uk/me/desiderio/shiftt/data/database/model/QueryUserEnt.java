package uk.me.desiderio.shiftt.data.database.model;

import com.twitter.sdk.android.core.models.User;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

/**
 * Room Entity to hold {@link User} data in the {@link TweetEnt} query
 */
public class QueryUserEnt {

    @Embedded
    public UserEnt userEnt;


    @Relation(parentColumn = "status_id",
            entityColumn = "id",
            entity = TweetEnt.class)
    public List<TweetEnt> tweetEntList;

    @Ignore
    public UserEnt getUserEnt() {
        if (tweetEntList != null && !tweetEntList.isEmpty()) {
            userEnt.status = tweetEntList.get(0);
        }
        return userEnt;
    }
}
