package uk.me.desiderio.shiftt.data.network;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import uk.me.desiderio.shiftt.data.network.model.Place;
import uk.me.desiderio.shiftt.data.network.model.Trend;
import uk.me.desiderio.shiftt.data.network.model.TrendsQueryResult;
import uk.me.desiderio.shiftt.di.ForApplication;
import uk.me.desiderio.shiftt.utils.AppExecutors;

@Singleton
public class TwitterNetworkDataSource {

    public static final String KILOMETERS = "km";
    public static final String MILES = "mi";
    private static final String TAG = TwitterNetworkDataSource.class.getSimpleName();

    private Context context;
    private AppExecutors appExecutors;
    private ShifttTwitterApiClient customApiClient;
    private TwitterApiClient twitterApiClient;

    private MutableLiveData<List<Tweet>> tweetListLiveData;
    private MutableLiveData<List<Trend>> trendListLiveData;

    @Inject
    public TwitterNetworkDataSource(@ForApplication Context context,
                                    AppExecutors appExecutors,
                                    ShifttTwitterApiClient customApiClient,
                                    TwitterApiClient twitterApiClient) {
        this.context = context;
        this.appExecutors = appExecutors;
        this.customApiClient = customApiClient;
        this.twitterApiClient = twitterApiClient;

        this.tweetListLiveData = new MutableLiveData<>();
        this.trendListLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<List<Tweet>> getTweetList() {
        return tweetListLiveData;
    }

    public MutableLiveData<List<Trend>> getTrendList() {
        return trendListLiveData;
    }

    public void updateTweetData(List<Tweet> tweets) {
        tweetListLiveData.setValue(tweets);
    }

    // requests

    // todo make sure you need this method with two signatures
    public void fetchTweetsOnLocation(double lat,
                                      double lng,
                                      String trendName,
                                      String radiusSize,
                                      @TwitterRadiusUnit String radiusUnit) {
        Intent intent = TwitterIntentService.getSearchTweetsByLocationAndTrendIntent(context,
                                                                                     lat,
                                                                                     lng,
                                                                                     trendName,
                                                                                     radiusSize,
                                                                                     radiusUnit);
        context.startService(intent);
    }

    public void fetchTrendsByLocation(double lat,
                                      double lng) {
        Intent intent = TwitterIntentService.getClosestPlacesByLocationIntent(context,
                                                                              lat,
                                                                              lng);
        context.startService(intent);
    }

    void requestClosestPlacesByLocation(double lat, double lng) {
        Log.d(TAG, "requestClosestPlacesByLocation: request: " + lat + " : " + lng);

        ClosestPlacesByLocationService statusesService = customApiClient.getClosestPlacesByLocationService();
        Call<List<Place>> call = statusesService.closest(lat, lng);
        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void success(Result<List<Place>> result) {
                Log.d(TAG, "requestClosestPlacesByLocation: success: " + result.data.get(0).name);

                // todo add checks for empty response!!
                result.data.stream()
                        .forEach(place -> {
                            Log.d(TAG, " closest places :: success: " + place.woeid);
                            requestTrendsByPlaceId(place);
                        });
            }

            public void failure(TwitterException exception) {
                // todo implement Trends request failure
                Log.d(TAG, "=====> failure: " + exception.getMessage());
            }
        });
    }

    // build and make request to search for tweets based on location
    void requestTweetsOnTrendNameAndGeocode(String trendName,
                                            double lat,
                                            double lng,
                                            String radiusSize,
                                            @TwitterRadiusUnit String radiusUnit) {

        Geocode.Distance distance = getGeocodeDistance(radiusUnit);
        Geocode geocode = getGeocode(lat, lng, radiusSize, distance);
        Log.d(TAG, "Neighbourhood Search: request: distance: " + distance + " geocode: " + geocode);

        Call<Search> call = getSearchCall(trendName, geocode);
        call.enqueue(new Callback<Search>() {
            @Override
            public void success(Result<Search> result) {
                Log.d(TAG, "Neighbourhood Search: success: ");
                updateTweetData(result.data.tweets);
            }

            public void failure(TwitterException exception) {
                // todo implement Neighbourhood Search failure
                Log.d(TAG, "turmeric failure: " + exception.getMessage());
            }
        });
    }

    void requestTrendsByPlaceId(@NonNull final Place place) {
        TrendsByPlaceIdService statusesService = customApiClient.getTrendsByPlaceService();
        Call<List<TrendsQueryResult>> call = statusesService.place(place.woeid);
        Log.d(TAG, "requestTrendsByPlaceId: request: woeid" + place.woeid);
        call.enqueue(new Callback<List<TrendsQueryResult>>() {
            @Override
            public void success(Result<List<TrendsQueryResult>> result) {
                Log.d(TAG, " chandelier requestTrendsByPlaceId: success: ");

                // todo temporary fix REMOVE! this should be db inserction
                List<Trend> trendList = new ArrayList<>();

                result.data.stream()
                        .forEach(trendsQuerys -> {
                            trendsQuerys.trends.stream()
                                    .forEach(trend -> trend.place = place);
                            trendList.addAll(trendsQuerys.trends);
                        });

                trendListLiveData.setValue(trendList);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d(TAG, "requestTrendsByPlaceId: success: ");

            }
        });
    }

    private Call<Search> getSearchCall(String trendName, Geocode geocode) {
        SearchService searchService = twitterApiClient.getSearchService();
        return searchService.tweets(trendName,
                                    geocode,
                                    null,
                                    null,
                                    null,
                                    100,
                                    null,
                                    null,
                                    null,
                                    true);
    }

    private Geocode getGeocode(double lat,
                               double lng,
                               String radiusSize,
                               Geocode.Distance distance) {
        int radiusInt = Integer.parseInt(radiusSize);
        return new Geocode(lat, lng, radiusInt, distance);
    }

    private Geocode.Distance getGeocodeDistance(@TwitterRadiusUnit String radiusUnit) {
        if (KILOMETERS.equals(radiusUnit)) {
            return Geocode.Distance.KILOMETERS;
        } else if (MILES.equals(radiusUnit)) {
            return Geocode.Distance.MILES;
        } else {
            throw new IllegalArgumentException("Generating Geocode : only 'km' or 'ml' " +
                                                       "String values allowed");
        }
    }
    @StringDef({KILOMETERS, MILES})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TwitterRadiusUnit {
    }

}
