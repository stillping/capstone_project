package uk.me.desiderio.shiftt.data.network;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.annotation.VisibleForTesting;
import dagger.android.AndroidInjection;
import uk.me.desiderio.shiftt.data.network.TwitterNetworkDataSource.TwitterRadiusUnit;

/**
 *
 */
public class TwitterIntentService extends IntentService {

    @VisibleForTesting
    public static final String EXTRA_LATITUDE = "extra_latititude";
    @VisibleForTesting
    public static final String EXTRA_LONGITUDE = "longitude";
    @VisibleForTesting
    public static final String EXTRA_TREND_NAME = "trend_name";
    @VisibleForTesting
    public static final String EXTRA_RADIUS_SIZE = "search_radius_size";
    @VisibleForTesting
    public static final String EXTRA_RADIUS_UNIT = "search_radius_unit";


    private static final String SERVICE_NAME = TwitterIntentService.class.getSimpleName();

    @Inject
    public TwitterNetworkDataSource dataSource;

    @Inject
    public TwitterIntentService() {
        super(SERVICE_NAME);
    }


    public static Intent getClosestPlacesByLocationIntent(@NonNull Context context,
                                                          @NonNull double lat,
                                                          @NonNull double lon) {
        Intent intent = new Intent(context, TwitterIntentService.class);
        intent.setAction(TASK_TRENDS_BY_LOCATION);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lon);

        return intent;
    }

    public static Intent getSearchTweetsByLocationAndTrendIntent(@NonNull Context context,
                                                                 @NonNull double lat,
                                                                 @NonNull double lon,
                                                                 @Nullable String trendName,
                                                                 @NonNull String radiusSize,
                                                                 @TwitterRadiusUnit
                                                                 @NonNull String radiusUnit) {
        Intent intent = new Intent(context, TwitterIntentService.class);
        intent.setAction(TASK_SEARCH_TWEETS_ON_LOCATION);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lon);

        if (trendName != null) {
            intent.putExtra(EXTRA_TREND_NAME, trendName);
        }

        intent.putExtra(EXTRA_RADIUS_SIZE, radiusSize);
        intent.putExtra(EXTRA_RADIUS_UNIT, radiusUnit);


        return intent;
    }

    @Override
    public void onCreate() {
        AndroidInjection.inject(this);
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        @TwitterServiceTask
        String task = intent.getAction();

        double lat = intent.getDoubleExtra(EXTRA_LATITUDE, 0d);
        double lng = intent.getDoubleExtra(EXTRA_LONGITUDE, 0d);

        switch (task) {
            case TASK_SEARCH_TWEETS_ON_LOCATION:
                Log.d(SERVICE_NAME, "onHandleIntent: TASK_SEARCH_TWEETS_ON_LOCATION");

                String trendName = intent.getStringExtra(EXTRA_TREND_NAME);
                String radiusSize = intent.getStringExtra(EXTRA_RADIUS_SIZE);
                String radiusUnit = intent.getStringExtra(EXTRA_RADIUS_UNIT);

                dataSource.requestTweetsOnTrendNameAndGeocode(trendName,
                                                              lat, lng,
                                                              radiusSize,
                                                              radiusUnit);
                break;
            case TASK_TRENDS_BY_LOCATION:
                // initially request place by location then a second reques is triggered
                // for the trends in each of the response places
                Log.d(SERVICE_NAME, "onHandleIntent: TASK_CLOSEST_PLACES_BY_LOCATION");
                dataSource.requestClosestPlacesByLocation(lat, lng);
                break;
        }
    }

    @StringDef({TASK_SEARCH_TWEETS_ON_LOCATION,
            TASK_TRENDS_BY_LOCATION})
    @Retention(RetentionPolicy.SOURCE)
    @interface TwitterServiceTask { }
    @VisibleForTesting
    public static final String TASK_TRENDS_BY_LOCATION = "task_trends_by_place_id";
    @VisibleForTesting
    public static final String TASK_SEARCH_TWEETS_ON_LOCATION =
            "task_search_tweets_on_location";

}
