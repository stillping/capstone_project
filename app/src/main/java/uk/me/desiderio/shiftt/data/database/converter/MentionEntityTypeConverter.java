package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.models.MentionEntity;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;
import uk.me.desiderio.shiftt.data.database.model.TweetEntitiesEnt;

/**
 * Room {@link TypeConverter} for the {@link TweetEntitiesEnt} 'userMentions' property
 */
public class MentionEntityTypeConverter {
    @TypeConverter
    public static List<MentionEntity> stringToMentionEntitylList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<MentionEntity>>() {
        }.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String metionEntityListToString(List<MentionEntity> mentionEntities) {
        return new Gson().toJson(mentionEntities);
    }
}
