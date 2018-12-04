package uk.me.desiderio.shiftt.data.database.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.twitter.sdk.android.core.models.Card;

import java.lang.reflect.Type;

import androidx.room.TypeConverter;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;

/**
 * Room {@link TypeConverter} for the {@link TweetEnt} '' property
 */
public class CardTypeConverter {
    @TypeConverter
    public static Card stringToCard(String data) {
        if (data == null) {
            return null;
        }

        Type type = new TypeToken<Card>() {
        }.getType();

        return new Gson().fromJson(data, type);
    }

    @TypeConverter
    public static String cardToString(Card card) {
        return new Gson().toJson(card);
    }
}
