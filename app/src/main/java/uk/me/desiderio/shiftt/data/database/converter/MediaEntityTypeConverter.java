package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.models.MediaEntity;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;
import uk.me.desiderio.shiftt.data.database.model.TweetEntitiesEnt;

/**
 * Room {@link TypeConverter} for the {@link TweetEntitiesEnt} 'media' property
 */
public class MediaEntityTypeConverter {
    @TypeConverter
    public static List<MediaEntity> stringToMediaEntitylList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<MediaEntity>>() {
        }.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String mediaEntityListToString(List<MediaEntity> mediaEntities) {
        return new Gson().toJson(mediaEntities);
    }
}
