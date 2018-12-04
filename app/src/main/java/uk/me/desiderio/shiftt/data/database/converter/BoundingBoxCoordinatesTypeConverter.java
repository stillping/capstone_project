package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import androidx.room.TypeConverter;
import uk.me.desiderio.shiftt.data.database.model.BoundingBoxEnt;

/**
 * Room {@link TypeConverter} for the {@link BoundingBoxEnt} 'coordinates' properties
 */
public class BoundingBoxCoordinatesTypeConverter {
    @TypeConverter
    public static List<List<List<Double>>> stringToCoordinatesList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }

        Type listType = new TypeToken<List<List<List<Double>>>>() {
        }.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String coordinatesListToString(List<List<List<Double>>> coordinatesList) {
        return new Gson().toJson(coordinatesList);
    }
}
