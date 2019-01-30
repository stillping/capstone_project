package uk.me.desiderio.shiftt.data.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import okhttp3.Headers;
import retrofit2.Call;
import uk.me.desiderio.shiftt.data.database.TrendsDao;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.network.ApiCallback;
import uk.me.desiderio.shiftt.data.network.ApiResponse;
import uk.me.desiderio.shiftt.data.network.ClosestPlacesByLocationService;
import uk.me.desiderio.shiftt.data.network.GatheringApiCallback;
import uk.me.desiderio.shiftt.data.network.ShifttTwitterApiClient;
import uk.me.desiderio.shiftt.data.network.TrendsByPlaceIdService;
import uk.me.desiderio.shiftt.data.network.model.Place;
import uk.me.desiderio.shiftt.data.network.model.TrendsQueryResult;
import uk.me.desiderio.shiftt.util.AppExecutors;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;

import static uk.me.desiderio.shiftt.data.repository.RateLimiter.TREND_PLACE_KEY_NAME;

/**
 * Repository to request Trends
 *
 * It uses {@link NetworkBoundBiResouce} as in order to retrieve the available trends
 * two consecutive request has to be done:
 * - Twitter's closest API request to retrieve of the "places" near to user location.
 * - Twitter's place API request to retrieve all the trends available in places provided as
 * a parameter
 *
 */
public class TrendsRepository {

    private final AppExecutors appExecutors;

    private final TrendsDao trendsDao;
    private final ShifttTwitterApiClient customApiClient;
    private final ApiCallback<List<Place>> closestApiCallback;
    private final GatheringApiCallback.Factory<List<TrendsQueryResult>> placeApiCallbackFactory;

    private final RateLimitsRepository rateLimitsRepository;

    private final ConnectivityLiveData connectivityLiveData;


    @Inject
    public TrendsRepository(TrendsDao trendsDao,
                            AppExecutors appExecutors,
                            ShifttTwitterApiClient customApiClient,
                            ApiCallback<List<Place>> closestApiCallback,
                            GatheringApiCallback.Factory<List<TrendsQueryResult>>
                                    placeApiCallbackFactory,
                            RateLimitsRepository rateLimitsRepository,
                            ConnectivityLiveData connectivityLiveData) {

        this.trendsDao = trendsDao;
        this.appExecutors = appExecutors;
        this.customApiClient = customApiClient;
        this.closestApiCallback = closestApiCallback;
        this.placeApiCallbackFactory = placeApiCallbackFactory;
        this.rateLimitsRepository = rateLimitsRepository;
        this.connectivityLiveData = connectivityLiveData;
    }

    public LiveData<Resource<List<TrendEnt>>> getAllTrendsByLocation(double lat,
                                                                     double lng,
                                                                     long locationTime) {
        return new NetworkBoundBiResouce<List<Place>, List<TrendsQueryResult>, List<TrendEnt>>
                (appExecutors,
                 connectivityLiveData) {
            @Override
            protected void saveCallResult(List<TrendsQueryResult> item) {
                // todo inject place associated with the trend
                List<TrendEnt> trendEnts = parseTrendData(null, item);
                trendsDao.insertTrendEntList(trendEnts);
            }

            @Override
            protected void saveHeaderInfo(Headers headers) {
                rateLimitsRepository.updateRateLimit(TREND_PLACE_KEY_NAME, lat, lng, headers);
            }

            @Override
            protected boolean shouldFetch(@Nullable List<TrendEnt> data) {
                return data == null || shouldFetchOnLimits(lat, lng, locationTime);
            }

            @Override
            protected LiveData<List<TrendEnt>> loadFromDb() {
                return trendsDao.getAllTrends();
            }

            @Override
            protected LiveData<ApiResponse<List<Place>>> createInitialCall() {

                appExecutors.getNetworkIO().execute(() -> {
                    ClosestPlacesByLocationService closestPlacesService = customApiClient
                            .getClosestPlacesByLocationService();
                    Call<List<Place>> placeCall = closestPlacesService.closest(lat, lng);
                    // todo add network executors
                    placeCall.enqueue(closestApiCallback);
                });

                return closestApiCallback.getResponse();
            }

            @Override
            protected LiveData<ApiResponse<List<TrendsQueryResult>>> createCall(List<Place>
                                                                                        placeList) {

                GatheringApiCallback<List<TrendsQueryResult>> gatheringApiCallback =
                        placeApiCallbackFactory.getApiCallback(placeList.size());

                appExecutors.getNetworkIO().execute(() -> {
                    TrendsByPlaceIdService trendsByPlaceIdService = customApiClient
                            .getTrendsByPlaceService();

                    placeList.forEach(place -> {
                        Call<List<TrendsQueryResult>> call =
                                trendsByPlaceIdService.place(place.woeid);
                        call.enqueue(gatheringApiCallback);
                    });
                });

                return gatheringApiCallback.getResponse();
            }
        }.asLiveData();
    }

    private List<TrendEnt> parseTrendData(Place place, List<TrendsQueryResult> trendResultList) {
        List<TrendEnt> trendList = new ArrayList<>();

        trendResultList
                .forEach(trendsQuerys -> {
                    if (trendsQuerys != null && trendsQuerys.trends != null) {
                        List<TrendEnt> queryTrendEnts = trendsQuerys.trends.stream()
                                .map(trend -> {
                                    // todo inject place associated with trend
                                    trend.place = place;
                                    return new TrendEnt(trend);
                                })
                                .collect(Collectors.toList());
                        trendList.addAll(queryTrendEnts);
                    }
                });
        return trendList;
    }

    private boolean shouldFetchOnLimits(double lat, double lng, long locTime) {
        return rateLimitsRepository.shouldFetch(TREND_PLACE_KEY_NAME,
                                                lat,
                                                lng,
                                                locTime);
    }
}
