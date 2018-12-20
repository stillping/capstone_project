package uk.me.desiderio.shiftt.data;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.twitter.sdk.android.core.models.Tweet;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import uk.me.desiderio.shiftt.data.database.TrendsDao;
import uk.me.desiderio.shiftt.data.database.TweetsDao;
import uk.me.desiderio.shiftt.data.database.model.PlaceEnt;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;
import uk.me.desiderio.shiftt.data.location.LocationLiveData;
import uk.me.desiderio.shiftt.data.network.TwitterNetworkDataSource;
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

    private static final String TAG = ShifttRepository.class.getSimpleName();
    private final FusedLocationProviderClient fusedLocationProviderClient;

    private final TwitterNetworkDataSource dataSource;
    private final AppExecutors appExecutors;
    private final ShifttSharedPreferences sharedPreferences;
    private MediatorLiveData<LocationViewData> locationLiveData;
    private final TweetsDao tweetsDao;
    private final TrendsDao trendsDao;

    // todo HERE set current time and define stale. based on that request start the location live
    // data
    @Inject
    public ShifttRepository(TwitterNetworkDataSource dataSource,
                            TweetsDao tweetsDao,
                            TrendsDao trendsDao,
                            AppExecutors appExecutors,
                            FusedLocationProviderClient fusedLocationProviderClient,
                            ShifttSharedPreferences sharedPreferences) {

        this.dataSource = dataSource;
        this.tweetsDao = tweetsDao;
        this.trendsDao = trendsDao;
        this.appExecutors = appExecutors;
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.sharedPreferences = sharedPreferences;


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

        initLocationViewData();
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
    private void initLocationViewData() {
        locationLiveData = new MediatorLiveData<>();
        locationLiveData.observeForever(locationViewData -> startFetchTweetDataByLocation(null));
    }

    private LocationViewData retrieveLocationViewDataFromPreferences() {
        double lat = sharedPreferences.getLastKnownLatitude();
        double lon = sharedPreferences.getLastKnownLongitude();
        long time = sharedPreferences.getLastKnownLocationTime();

        // TODO add logic to decide whether the data is stale or not based on time
        return new LocationViewData(lat, lon, false);
    }

    // Shared Preference

    private void persistLLatestKnownLocation(Location location) {
        sharedPreferences.setLastKnownLocation(location.getLatitude(),
                                               location.getLongitude(),
                                               location.getTime());
    }

    // Data Source

    private void startFetchTweetDataByLocation(String trendName) {
        double lat = sharedPreferences.getLastKnownLatitude();
        double lon = sharedPreferences.getLastKnownLongitude();
        String radiusUnit = sharedPreferences.getSearchRadiusUnits();
        String radiusSize = sharedPreferences.getSearchRadiusSize();

        // TODO check for valid coordinates values - it couldn't be 200

        if (lat == COOR_DEFAULT_VALUE || lon == COOR_DEFAULT_VALUE) {
            return;
        }

        dataSource.fetchTweetsOnLocation(lat,
                                         lon,
                                         trendName,
                                         radiusSize,
                                         radiusUnit);


        // TODO add request to twitter service with above parameters

    }

    private void fetchTrendsOnLocation() {
        double lat = sharedPreferences.getLastKnownLatitude();
        double lon = sharedPreferences.getLastKnownLongitude();

        // TODO check for valid coordinates values - it couldn't be 200

        dataSource.fetchTrendsByLocation(lat, lon);

        // TODO add request to twitter service with above parameters
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
}
