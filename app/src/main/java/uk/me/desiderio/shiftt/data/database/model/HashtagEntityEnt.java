package uk.me.desiderio.shiftt.data.database.model;

import com.twitter.sdk.android.core.models.HashtagEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room entity class for the {@link HashtagEntity} Twitter data object
 */
@Entity(tableName = "hashtag",
        indices = {@Index(value = "text", unique = true)})
public class HashtagEntityEnt extends EntityEnt {

    @NonNull
    @PrimaryKey
    public final String text;

    public HashtagEntityEnt(String text, List<Integer> indices) {
        super(indices);
        this.text = text;
    }

    @Ignore
    public HashtagEntityEnt(HashtagEntity hashtagEntity) {
        this(hashtagEntity.text, hashtagEntity.indices);
    }

    public HashtagEntity getSeed() {
        return new HashtagEntity(text, indices.get(0), indices.get(1));
    }
}
