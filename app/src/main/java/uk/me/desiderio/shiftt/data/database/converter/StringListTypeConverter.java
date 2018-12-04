package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;

/**
 * Room {@link TypeConverter} for any list of Strings
 */
public class StringListTypeConverter {
    @TypeConverter
    public static List<String> stringToStringList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<String>>() {
        }.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String stringListToString(List<String> strings) {
        return new Gson().toJson(strings);
    }
}
