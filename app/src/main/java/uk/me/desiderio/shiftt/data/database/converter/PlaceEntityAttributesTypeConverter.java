package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import androidx.room.TypeConverter;
import uk.me.desiderio.shiftt.data.database.model.PlaceEnt;

/**
 * Room {@link TypeConverter} for the {@link PlaceEnt} 'attributes' property
 */
public class PlaceEntityAttributesTypeConverter {
    @TypeConverter
    public static Map<String, String> stringToAttributesList(String data) {
        if (data == null) {
            return Collections.emptyMap();
        }

        Type listType = new TypeToken<Map<String, String>>() {
        }.getType();

        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String attributesListToString(Map<String, String> attributes) {
        return new Gson().toJson(attributes);
    }
}
