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

    public static String LATITUDE_PREFERENCE;
    public static String LONGITUDE_PREFERENCE;
    public static String LOCATION_TIME_PREFERENCE;

    public static final double COOR_DEFAULT_VALUE = 200;

    private Context context;

    @Inject
    public ShifttSharedPreferences(@ForApplication Context context) {
        this.context = context;
        LATITUDE_PREFERENCE = context.getString(R.string.pref_location_lat_key);
        LONGITUDE_PREFERENCE = context.getString(R.string.pref_location_lon_key);
        LOCATION_TIME_PREFERENCE = context.getString(R.string.pref_location_time_key);
    }

    public void setLastKnownLocation(double lat, double lon, long time) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(LATITUDE_PREFERENCE, Double.doubleToRawLongBits(lat));
        editor.putLong(LONGITUDE_PREFERENCE, Double.doubleToRawLongBits(lon));
        editor.putLong(LOCATION_TIME_PREFERENCE, time);
        editor.apply();

    }

    public double getLastKnownLatitude() {
        return Double.longBitsToDouble(getSharedPreferences()
                                               .getLong(LATITUDE_PREFERENCE,
                                                        Double.doubleToLongBits(COOR_DEFAULT_VALUE)));
    }

    public double getLastKnownLongitude() {
        return Double.longBitsToDouble(getSharedPreferences()
                                               .getLong(LONGITUDE_PREFERENCE,
                                                        Double.doubleToLongBits(COOR_DEFAULT_VALUE)));
    }

    public long getLastKnownLocationTime() {
        return getSharedPreferences().getLong(LOCATION_TIME_PREFERENCE, 0);
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
