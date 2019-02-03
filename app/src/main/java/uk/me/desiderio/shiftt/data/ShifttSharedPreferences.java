package uk.me.desiderio.shiftt.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.di.ForApplication;

/**
 * Utility class to store application's {@link SharedPreferences}
 */
public class ShifttSharedPreferences {

    private static String PREF_KEY_LATITUDE;
    private static String PREF_KEY_LONGITUDE;
    private static String PREF_KEY_LOCATION_TIME;
    private static String PREF_KEY_SEARCH_RADIUS_UNITS;
    private static String PREF_KEY_SEARCH_RADIUS_SIZE;
    private static String PREF_DEFAULT_SEARCH_RADIUS_UNITS;
    private static String PREF_DEFAULT_SEARCH_RADIUS_SIZE;


    // TODO add check for default values in order to not carry out request
    public static final double COOR_DEFAULT_VALUE = 200;

    private final Context context;

    @Inject
    public ShifttSharedPreferences(@ForApplication Context context) {
        this.context = context;
        PREF_KEY_LATITUDE = context.getString(R.string.pref_location_lat_key);
        PREF_KEY_LONGITUDE = context.getString(R.string.pref_location_lon_key);
        PREF_KEY_LOCATION_TIME = context.getString(R.string.pref_location_time_key);
        PREF_KEY_SEARCH_RADIUS_UNITS = context.getString(R.string.pref_key_twitter_radius_unit_key);
        PREF_KEY_SEARCH_RADIUS_SIZE = context.getString(R.string.pref_key_twitter_radius_size_key);
        PREF_DEFAULT_SEARCH_RADIUS_SIZE = context.getString(R.string
                                                              .pref_twitter_default_search_radius_size);
        PREF_DEFAULT_SEARCH_RADIUS_UNITS = context.getString(R.string
                                                              .pref_twitter_default_search_radius_units);
    }

    public void setLastKnownLocation(double lat, double lon, long time) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(PREF_KEY_LATITUDE, Double.doubleToRawLongBits(lat));
        editor.putLong(PREF_KEY_LONGITUDE, Double.doubleToRawLongBits(lon));
        editor.putLong(PREF_KEY_LOCATION_TIME, time);
        editor.apply();

    }

    public double getLastKnownLatitude() {
        return Double.longBitsToDouble(getSharedPreferences()
                                               .getLong(PREF_KEY_LATITUDE,
                                                        Double.doubleToLongBits(COOR_DEFAULT_VALUE)));
    }

    public double getLastKnownLongitude() {
        return Double.longBitsToDouble(getSharedPreferences()
                                               .getLong(PREF_KEY_LONGITUDE,
                                                        Double.doubleToLongBits(COOR_DEFAULT_VALUE)));
    }

    public long getLastKnownLocationTime() {
        return getSharedPreferences().getLong(PREF_KEY_LOCATION_TIME, 0);
    }

    public String getSearchRadiusUnits() {
        return getSharedPreferences().getString(PREF_KEY_SEARCH_RADIUS_UNITS,
                                                PREF_DEFAULT_SEARCH_RADIUS_UNITS);
    }

    public String getSearchRadiusSize() {
        return getSharedPreferences().getString(PREF_KEY_SEARCH_RADIUS_SIZE,
                                                PREF_DEFAULT_SEARCH_RADIUS_SIZE);
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
