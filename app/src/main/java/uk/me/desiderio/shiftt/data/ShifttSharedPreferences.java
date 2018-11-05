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

    public static String PREF_LATITUDE;
    public static String PREF_LONGITUDE;
    public static String PREF_LOCATION_TIME;
    public static String PREF_SEARCH_RADIUS_UNITS;
    public static String PREF_SEARCH_RADIUS_SIZE;


    public static final double COOR_DEFAULT_VALUE = 200;

    private Context context;

    @Inject
    public ShifttSharedPreferences(@ForApplication Context context) {
        this.context = context;
        PREF_LATITUDE = context.getString(R.string.pref_location_lat_key);
        PREF_LONGITUDE = context.getString(R.string.pref_location_lon_key);
        PREF_LOCATION_TIME = context.getString(R.string.pref_location_time_key);
        PREF_SEARCH_RADIUS_UNITS = context.getString(R.string.pref_key_twitter_radius_unit_key);
        PREF_SEARCH_RADIUS_SIZE = context.getString(R.string.pref_key_twitter_radius_size_key);
    }

    public void setLastKnownLocation(double lat, double lon, long time) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(PREF_LATITUDE, Double.doubleToRawLongBits(lat));
        editor.putLong(PREF_LONGITUDE, Double.doubleToRawLongBits(lon));
        editor.putLong(PREF_LOCATION_TIME, time);
        editor.apply();

    }

    public double getLastKnownLatitude() {
        return Double.longBitsToDouble(getSharedPreferences()
                                               .getLong(PREF_LATITUDE,
                                                        Double.doubleToLongBits(COOR_DEFAULT_VALUE)));
    }

    public double getLastKnownLongitude() {
        return Double.longBitsToDouble(getSharedPreferences()
                                               .getLong(PREF_LONGITUDE,
                                                        Double.doubleToLongBits(COOR_DEFAULT_VALUE)));
    }

    public long getLastKnownLocationTime() {
        return getSharedPreferences().getLong(PREF_LOCATION_TIME, 0);
    }

    public String getSearchRadiusUnits() {
        return getSharedPreferences().getString(PREF_SEARCH_RADIUS_UNITS, "km");
    }

    public String getSearchRadiusSize() {
        return getSharedPreferences().getString(PREF_SEARCH_RADIUS_SIZE, "3");
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
