package uk.me.desiderio.shiftt.data.database.model;

import com.twitter.sdk.android.core.models.Tweet;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity class to hold data for the {@link Tweet} {@code current_user_retweet} property object
 */
@Entity
public class CurrentUserRetweetEnt {

    @PrimaryKey
    public final Long id;
    public final String id_str;

    public CurrentUserRetweetEnt(Long id, String id_str) {
        this.id = id;
        this.id_str = id_str;
    }
}
