package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;

/**
 * Room {@link TypeConverter} for any list of {@link Integer}
 */
public class IntegerListTypeConverter {
    @TypeConverter
    public static List<Integer> stringToIntegerList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Integer>>() {
        }.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String integerListToString(List<Integer> integers) {
        return new Gson().toJson(integers);
    }
}
