package uk.me.desiderio.shiftt.data;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import uk.me.desiderio.shiftt.data.database.RateLimitDao;
import uk.me.desiderio.shiftt.data.database.TrendsDao;
import uk.me.desiderio.shiftt.data.database.TweetsDao;
import uk.me.desiderio.shiftt.data.database.model.PlaceEnt;
import uk.me.desiderio.shiftt.data.database.model.RateLimitEnt;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;
import uk.me.desiderio.shiftt.data.location.LocationLiveData;
import uk.me.desiderio.shiftt.data.network.RateLimiter;
import uk.me.desiderio.shiftt.data.network.ShifttTwitterApiClient;
import uk.me.desiderio.shiftt.data.network.TwitterNetworkDataSource;
import uk.me.desiderio.shiftt.data.network.model.RateLimit;
import uk.me.desiderio.shiftt.data.network.model.Trend;
import uk.me.desiderio.shiftt.ui.model.LocationViewData;
import uk.me.desiderio.shiftt.ui.model.MapItem;
import uk.me.desiderio.shiftt.utils.AppExecutors;

import static uk.me.desiderio.shiftt.data.ShifttSharedPreferences.COOR_DEFAULT_VALUE;

/**
 * Handles application's data operations
 */
@Singleton
public class ShifttRepository {

    // TODO: 19/12/2018 Clear DATABASE

    // TODO: 20/12/2018 timeout data

    // TODO: 20/12/2018 add checks for connectivity

    // TODO: 22/12/2018 let the ui know if data served is stale : location or tweets for
    // different location

    private static final String TAG = ShifttRepository.class.getSimpleName();
    private final FusedLocationProviderClient fusedLocationProviderClient;

    private final TwitterNetworkDataSource dataSource;
    private final AppExecutors appExecutors;
    private final ShifttSharedPreferences sharedPreferences;
    private final TweetsDao tweetsDao;
    private final TrendsDao trendsDao;
    private final RateLimitDao rateLimitDao;
    private MediatorLiveData<LocationViewData> locationLiveData;

    private RateLimiter rateLimiter;

    @Inject
    public ShifttRepository(TwitterNetworkDataSource dataSource,
                            TweetsDao tweetsDao,
                            TrendsDao trendsDao,
                            RateLimitDao rateLimitDao,
                            AppExecutors appExecutors,
                            FusedLocationProviderClient fusedLocationProviderClient,
                            ShifttSharedPreferences sharedPreferences,
                            ShifttTwitterApiClient customApiClient,
                            TwitterApiClient twitterApiClient) {

        this.dataSource = dataSource;
        this.tweetsDao = tweetsDao;
        this.trendsDao = trendsDao;
        this.rateLimitDao = rateLimitDao;
        this.appExecutors = appExecutors;
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.sharedPreferences = sharedPreferences;

        // no other data should be initialized until the ratelimits are in place
        initRateLimits();

        this.dataSource.getRateLimit().observeForever(rateLimit -> {
            rateLimiter.addLimit(rateLimit);
            persistRateLimit(rateLimit);
        });

        this.dataSource.getTweetList().observeForever(tweetList -> appExecutors.getDiskIO().execute(() -> {

            Log.d(TAG, "ShifttRepository: inserting all tweet entities");

            List<TweetEnt> tweetEntityList = getTweetEntList(tweetList);

            this.tweetsDao.insertTweetEntities(tweetEntityList);
        }));

        this.dataSource.getTrendList().observeForever(trends -> appExecutors.getDiskIO().execute(() -> {

            Log.d(TAG, "ShifttRepository: inserting all trends entities");

            List<TrendEnt> trendEntityList = getTrendEntList(trends);

            this.trendsDao.insertTrendEntList(trendEntityList);
        }));

        locationLiveData = new MediatorLiveData<>();
    }

    // Live Data

    public MutableLiveData<LocationViewData> getLocationLiveData() {
        return locationLiveData;
    }

    public LiveData<List<MapItem>> requestNeigbourhoodData() {
        LiveData<List<PlaceEnt>> places = tweetsDao.getAllPlaces();
        return new CombinedMapLiveData(places);
    }

    public LiveData<List<TrendEnt>> getTrendsListLiveData() {
        fetchTrendsOnLocation();
        return trendsDao.getAllTrends();
    }

    // TODO check if there is location services and connectivity
    public void getCurrentFusedLocation() {
        LocationLiveData locationLiveData = new LocationLiveData(fusedLocationProviderClient);

        this.locationLiveData.addSource(locationLiveData, location -> {
            if (location != null) {
                this.locationLiveData.removeSource(locationLiveData);
                persistLLatestKnownLocation(location);
                this.locationLiveData.setValue(retrieveLocationViewDataFromPreferences());
            }
        });
    }

    public void updateLastKnownLocation() {
        LocationViewData viewData = retrieveLocationViewDataFromPreferences();
        locationLiveData.setValue(viewData);
    }

    // Place

    // initialisation of locationViewDate
    // todo debug this is called twice at startup. The first time with the old location
    private void initLocationViewData() {
        locationLiveData.observeForever(locationViewData ->
                                                startFetchTweetDataByLocation(null));
    }

    private LocationViewData retrieveLocationViewDataFromPreferences() {
        double lat = sharedPreferences.getLastKnownLatitude();
        double lon = sharedPreferences.getLastKnownLongitude();
        long time = sharedPreferences.getLastKnownLocationTime();
        Log.d(TAG, "retrieveLocationViewDataFromPreferences: localitation " +
                lat + " : " + lon);

        // TODO add logic to decide whether the data is stale or not based on time
        return new LocationViewData(lat, lon, false);
    }

    // Shared Preference

    private void persistLLatestKnownLocation(Location location) {
        Log.d(TAG, "persistLLatestKnownLocation: localitation : " + location.getLatitude() + " : " +
                location.getLongitude() + " : " + location.getAccuracy());
        sharedPreferences.setLastKnownLocation(location.getLatitude(),
                                               location.getLongitude(),
                                               location.getTime() / 1000);
    }

    // Data Source

    private void startFetchTweetDataByLocation(String trendName) {
        double lat = sharedPreferences.getLastKnownLatitude();
        double lng = sharedPreferences.getLastKnownLongitude();
        long time = sharedPreferences.getLastKnownLocationTime();
        String radiusUnit = sharedPreferences.getSearchRadiusUnits();
        String radiusSize = sharedPreferences.getSearchRadiusSize();

        // TODO check for valid coordinates values - it couldn't be 200

        if (lat == COOR_DEFAULT_VALUE || lng == COOR_DEFAULT_VALUE) {
            return;
        }


        // TODO this could produce side effect that data available is stale
        if (rateLimiter.shouldFetch(RateLimiter.TWEETS_KEY_NAME, lat, lng, time)) {

            dataSource.fetchTweetsOnLocation(lat,
                                             lng,
                                             trendName,
                                             radiusSize,
                                             radiusUnit);
        }
    }

    private void fetchTrendsOnLocation() {
        double lat = sharedPreferences.getLastKnownLatitude();
        double lng = sharedPreferences.getLastKnownLongitude();
        long time = sharedPreferences.getLastKnownLocationTime();

        // TODO check for valid coordinates values - it couldn't be 200

        // TODO this could produce side effect that data available is stale
        if (rateLimiter.shouldFetch(RateLimiter.TREND_PLACE_KEY_NAME, lat, lng, time)) {
            dataSource.fetchTrendsByLocation(lat, lng);
        }
    }


    private List<TrendEnt> getTrendEntList(List<Trend> trendList) {
        return trendList.
                stream().
                map(TrendEnt::new).
                collect(Collectors.toList());
    }

    private List<TweetEnt> getTweetEntList(List<Tweet> tweetList) {
        return tweetList.
                stream().
                map(TweetEnt::new).
                collect(Collectors.toList());
    }

    // Rate Limits

    private void persistRateLimit(RateLimit limit) {
        RateLimitEnt rateLimitEnt = new RateLimitEnt(limit.name,
                                                     limit.coors,
                                                     limit.time,
                                                     limit.limit,
                                                     limit.remaining,
                                                     limit.reset);
        appExecutors.getDiskIO().execute(() -> rateLimitDao.insertRateLimit(rateLimitEnt));
    }

    private void initRateLimits() {
        rateLimiter = new RateLimiter();
        rateLimitDao.getAllRateLimits().observeForever(rateLimitList -> {
            rateLimitList.stream()
                    .forEach(rateLimitEnt -> rateLimiter.addLimit(rateLimitEnt.name,
                                                                  rateLimitEnt.coors,
                                                                  rateLimitEnt.time,
                                                                  rateLimitEnt.limit,
                                                                  rateLimitEnt.remaining,
                                                                  rateLimitEnt.reset));
            initLocationViewData();
        });
    }
}
