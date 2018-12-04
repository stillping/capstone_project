package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import androidx.room.TypeConverter;

/**
 * Room {@link TypeConverter} for any {@link Object}
 */
public class ObjectTypeConverter {
    @TypeConverter
    public static Object stringToObject(String data) {
        if (data == null) {
            return null;
        }

        Type type = new TypeToken<Object>() {
        }.getType();

        return new Gson().fromJson(data, type);
    }

    @TypeConverter
    public static String objectToString(Object object) {
        return new Gson().toJson(object);
    }
}
