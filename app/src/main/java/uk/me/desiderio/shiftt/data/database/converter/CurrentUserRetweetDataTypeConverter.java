package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import androidx.room.TypeConverter;
import uk.me.desiderio.shiftt.data.database.model.CurrentUserRetweetEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;

/**
 * Room {@link TypeConverter} for the {@link TweetEnt} 'currentUserRetweet' properties
 */
public class CurrentUserRetweetDataTypeConverter {
    @TypeConverter
    public static CurrentUserRetweetEnt stringToCurrentUserRetweetEnt(String data) {
        if (data == null) {
            return null;
        }

        Type type = new TypeToken<CurrentUserRetweetEnt>() {
        }.getType();

        return new Gson().fromJson(data, type);
    }

    @TypeConverter
    public static String currentUserRetweetEntToString(CurrentUserRetweetEnt currentUserRetweetEnt) {
        return new Gson().toJson(currentUserRetweetEnt);
    }
}
