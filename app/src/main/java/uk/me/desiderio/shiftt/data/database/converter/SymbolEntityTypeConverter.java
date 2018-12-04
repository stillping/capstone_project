package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.models.SymbolEntity;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;
import uk.me.desiderio.shiftt.data.database.model.TweetEntitiesEnt;

/**
 * Room {@link TypeConverter} for the {@link TweetEntitiesEnt} 'symbols' property
 */
public class SymbolEntityTypeConverter {
    @TypeConverter
    public static List<SymbolEntity> stringToSymbolEntityList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<SymbolEntity>>() {
        }.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String symbolEntityListToString(List<SymbolEntity> symbolEntities) {
        return new Gson().toJson(symbolEntities);
    }
}
