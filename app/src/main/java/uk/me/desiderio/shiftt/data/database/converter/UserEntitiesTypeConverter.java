package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.models.UserEntities;

import java.lang.reflect.Type;

import androidx.room.TypeConverter;
import uk.me.desiderio.shiftt.data.database.model.UserEnt;

/**
 * Room {@link TypeConverter} for the {@link UserEnt} 'entities' property
 */
public class UserEntitiesTypeConverter {
    @TypeConverter
    public static UserEntities stringToUserEntities(String data) {
        if (data == null) {
            return null;
        }
        Type type = new TypeToken<UserEntities>() {
        }.getType();
        return new Gson().fromJson(data, type);
    }

    @TypeConverter
    public static String userEntitiesToString(UserEntities userEntities) {
        return new Gson().toJson(userEntities);
    }
}
