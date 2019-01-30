package uk.me.desiderio.shiftt.data.network.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.StringDef;

/**
 * Twitter API query parameter values
 */
public class TwitterParams {


    // search area radius units
    public static final String KILOMETERS = "km";
    public static final String MILES = "mi";

    @StringDef({KILOMETERS, MILES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RadiusUnit {
    }

}
