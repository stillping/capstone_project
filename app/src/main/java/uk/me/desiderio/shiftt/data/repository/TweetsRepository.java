package uk.me.desiderio.shiftt.data.repository;

import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import okhttp3.Headers;
import retrofit2.Call;
import uk.me.desiderio.shiftt.data.CombinedMapLiveData;
import uk.me.desiderio.shiftt.data.database.TweetsDao;
import uk.me.desiderio.shiftt.data.database.model.PlaceEnt;
import uk.me.desiderio.shiftt.data.database.model.QueryTweetEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;
import uk.me.desiderio.shiftt.data.network.ApiCallback;
import uk.me.desiderio.shiftt.data.network.ApiResponse;
import uk.me.desiderio.shiftt.data.network.ShifttTwitterApiClient;
import uk.me.desiderio.shiftt.data.network.util.TwitterParams;
import uk.me.desiderio.shiftt.ui.model.MapItem;
import uk.me.desiderio.shiftt.util.AppExecutors;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;

import static uk.me.desiderio.shiftt.data.network.util.TwitterParams.KILOMETERS;
import static uk.me.desiderio.shiftt.data.network.util.TwitterParams.MILES;
import static uk.me.desiderio.shiftt.data.repository.RateLimiter.TWEETS_KEY_NAME;

/**
 *
 */
public class TweetsRepository {

    private final AppExecutors appExecutors;

    private final TweetsDao tweetsDao;
    private final ShifttTwitterApiClient twitterApiClient;
    private final ApiCallback<Search> searchApiCallback;

    private final RateLimitsRepository rateLimitsRepository;
    private final ConnectivityLiveData connectivityLiveData;


    @Inject
    public TweetsRepository(TweetsDao tweetsDao,
                            AppExecutors appExecutors,
                            ShifttTwitterApiClient twitterApiClient,
                            ApiCallback<Search> searchApiCallback,
                            RateLimitsRepository rateLimitsRepository,
                            ConnectivityLiveData connectivityLiveData) {
        this.tweetsDao = tweetsDao;
        this.appExecutors = appExecutors;
        this.twitterApiClient = twitterApiClient;
        this.searchApiCallback = searchApiCallback;
        this.rateLimitsRepository = rateLimitsRepository;
        this.connectivityLiveData = connectivityLiveData;
    }

    public LiveData<Resource<List<MapItem>>> getMapsItems(String trendName,
                                                          double lat,
                                                          double lng,
                                                          long locTime,
                                                          String radiusSize,
                                                          @TwitterParams.RadiusUnit
                                                                  String radiusUnit) {
        return new NetworkBoundResouce<Search, List<MapItem>>(appExecutors,
                                                              connectivityLiveData) {

            @Override
            protected void saveCallResult(Search item) {
                List<Tweet> tweetList = item.tweets;
                List<TweetEnt> tweetEntityList = getTweetEntList(tweetList);
                tweetsDao.insertTweetEntities(tweetEntityList);
            }

            @Override
            protected void saveHeaderInfo(Headers headers) {
                rateLimitsRepository.updateRateLimit(TWEETS_KEY_NAME, lat, lng, headers);

            }

            @Override
            protected boolean shouldFetch(@Nullable List<MapItem> data) {
                return hasNoData(data) || shouldFetchOnLimits(TWEETS_KEY_NAME,
                                                              lat,
                                                              lng,
                                                              locTime);
            }

            @Override
            protected LiveData<List<MapItem>> loadFromDb() {
                LiveData<List<PlaceEnt>> places = tweetsDao.getAllPlaces();
                return new CombinedMapLiveData(places);
            }

            @Override
            protected LiveData<ApiResponse<Search>> createCall() {

                appExecutors.getNetworkIO().execute(() -> {
                    Geocode.Distance distance = getGeocodeDistance(radiusUnit);
                    Geocode geocode = getGeocode(lat, lng, radiusSize, distance);

                    Call<Search> call = getSearchCall(trendName, geocode);
                    // todo add network executors
                    call.enqueue(searchApiCallback);
                });
                return searchApiCallback.getResponse();

            }
        }.asLiveData();
    }

    public LiveData<Resource<List<TweetEnt>>> getTweetsOnPlace(String placeFullName,
                                                               double lat,
                                                               double lng,
                                                               long locTime,
                                                               String radiusSize,
                                                               @TwitterParams.RadiusUnit
                                                                  String radiusUnit) {
        return new NetworkBoundResouce<Search, List<TweetEnt>>(appExecutors,
                                                              connectivityLiveData) {

            @Override
            protected void saveCallResult(Search item) {
                List<Tweet> tweetList = item.tweets;
                List<TweetEnt> tweetEntityList = getTweetEntList(tweetList);
                tweetsDao.insertTweetEntities(tweetEntityList);
            }

            @Override
            protected void saveHeaderInfo(Headers headers) {
                rateLimitsRepository.updateRateLimit(TWEETS_KEY_NAME, lat, lng, headers);

            }

            @Override
            protected boolean shouldFetch(@Nullable List<TweetEnt> data) {
                return hasNoData(data) || shouldFetchOnLimits(TWEETS_KEY_NAME,
                                                              lat,
                                                              lng,
                                                              locTime);
            }

            @Override
            protected LiveData<List<TweetEnt>> loadFromDb() {
                LiveData<List<QueryTweetEnt>> queries =
                        tweetsDao.getAllFeaturedPopTweetsEntQueryOnPlace(placeFullName);

                return Transformations.map(queries, queryTweetEntList ->
                    queryTweetEntList.stream()
                            .map(queryTweetEnt -> queryTweetEnt.getPopulatedTweetEnt())
                            .collect(Collectors.toList())
                );
            }

            @Override
            protected LiveData<ApiResponse<Search>> createCall() {
                appExecutors.getNetworkIO().execute(() -> {
                    Geocode.Distance distance = getGeocodeDistance(radiusUnit);
                    Geocode geocode = getGeocode(lat, lng, radiusSize, distance);

                    Call<Search> call = getSearchCall(null, geocode);
                    call.enqueue(searchApiCallback);
                });
                return searchApiCallback.getResponse();
            }
        }.asLiveData();
    }

    private boolean shouldFetchOnLimits(String limitName, double lat, double lng, long locTime) {
        return rateLimitsRepository.shouldFetch(limitName,
                                                lat,
                                                lng,
                                                locTime);
    }

    private boolean hasNoData(List data) {
        return data == null || data.isEmpty();
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

    private Geocode.Distance getGeocodeDistance(@TwitterParams.RadiusUnit String radiusUnit) {
        if (KILOMETERS.equals(radiusUnit)) {
            return Geocode.Distance.KILOMETERS;
        } else if (MILES.equals(radiusUnit)) {
            return Geocode.Distance.MILES;
        } else {
            throw new IllegalArgumentException("Generating Geocode : " +
                                                       "only 'km' or 'ml' String values are allowed");
        }
    }

    private static List<TweetEnt> getTweetEntList(List<Tweet> tweetList) {
        return tweetList.
                stream().
                map(TweetEnt::new).
                collect(Collectors.toList());
    }

}
