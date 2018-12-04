package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.models.UrlEntity;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;
import uk.me.desiderio.shiftt.data.database.model.TweetEntitiesEnt;

/**
 * Room {@link TypeConverter} for the {@link TweetEntitiesEnt} 'urls' property
 */
public class UrlEntityTypeConverter {
    @TypeConverter
    public static List<UrlEntity> stringToUrlEntityList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<UrlEntity>>() {
        }.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String urlEntityListToString(List<UrlEntity> urlEntities) {
        return new Gson().toJson(urlEntities);
    }
}
