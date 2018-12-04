package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;

/**
 * Room {@link TypeConverter} for any list of the {@link Double}
 */
public class DoubleListTypeConverter {
    @TypeConverter
    public static List<Double> stringToDoubleList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<Double>>() {
        }.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String doubleListToString(List<Double> doubles) {
        return new Gson().toJson(doubles);
    }
}
