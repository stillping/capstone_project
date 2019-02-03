package uk.me.desiderio.shiftt.data;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import retrofit2.Call;
import retrofit2.Response;
import uk.me.desiderio.shiftt.data.database.TrendsDao;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.network.ApiCallback;
import uk.me.desiderio.shiftt.data.network.ApiResponse;
import uk.me.desiderio.shiftt.data.network.ApiResponseFactory;
import uk.me.desiderio.shiftt.data.network.ClosestPlacesByLocationService;
import uk.me.desiderio.shiftt.data.network.GatheringApiCallback;
import uk.me.desiderio.shiftt.data.network.ShifttTwitterApiClient;
import uk.me.desiderio.shiftt.data.network.TrendsByPlaceIdService;
import uk.me.desiderio.shiftt.data.network.model.Place;
import uk.me.desiderio.shiftt.data.network.model.TrendsQueryResult;
import uk.me.desiderio.shiftt.data.repository.LocationRepository;
import uk.me.desiderio.shiftt.data.repository.RateLimitsRepository;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.data.repository.TrendsRepository;
import uk.me.desiderio.shiftt.data.util.InstantAppExecutors;
import uk.me.desiderio.shiftt.util.AppExecutors;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.me.desiderio.shiftt.data.mock.DataProvider.EXPECTED_PLACES_COUNT;
import static uk.me.desiderio.shiftt.data.mock.DataProvider.EXPECTED_TRENDS_COUNT;
import static uk.me.desiderio.shiftt.data.mock.DataProvider.EXPECTED_TRENDS_QUERY_COUNT;
import static uk.me.desiderio.shiftt.data.mock.DataProvider.EXPECTED_TREND_ENT_NAME;
import static uk.me.desiderio.shiftt.data.mock.DataProvider.getMockedClosestPlaceList;
import static uk.me.desiderio.shiftt.data.mock.DataProvider.getMockedTrendEnts;
import static uk.me.desiderio.shiftt.data.mock.DataProvider.getMockedTrendsQueryResults;

/**
 * Tests for {@link TrendsRepository}
 */
@RunWith(MockitoJUnitRunner.class)
public class TrendsRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock
    public Call<List<Place>> closestCall;
    @Mock
    public Call<List<TrendsQueryResult>> placesCall;
    @Mock
    private TrendsDao trendsDao;
    @Mock
    private
    ShifttTwitterApiClient customApiClient;
    @Mock
    private TrendsByPlaceIdService trendsByPlaceIdService;
    @Mock
    private ClosestPlacesByLocationService closestPlacesByLocationService;
    @Mock
    private ApiCallback<List<Place>> closestApiCallback;
    @Mock
    private GatheringApiCallback.Factory<List<TrendsQueryResult>> placesApiCallbackFactory;
    private GatheringApiCallback<List<TrendsQueryResult>> placesApiCallback;

    private TrendsRepository repository;

    private AppExecutors appExecutors;

    @Mock
    private ConnectivityLiveData connectivityLiveData;

    @Mock
    private RateLimitsRepository rateLimitsRepository;

    @Mock
    private LocationRepository locationRepository;

    @Before
    public void setUp() {
        appExecutors = InstantAppExecutors.getInstance();

        when(connectivityLiveData.getValue()).thenReturn(true);

        repository = new TrendsRepository(trendsDao,
                                          appExecutors,
                                          customApiClient,
                                          closestApiCallback,
                                          placesApiCallbackFactory,
                                          rateLimitsRepository,
                                          connectivityLiveData);

    }

    @Test
    public void loadTrendsFromNetwork() {
        MutableLiveData<List<TrendEnt>> dbData = new MutableLiveData<>();
        when(trendsDao.getAllTrends()).thenReturn(dbData);

        List<Place> placeList = getMockedClosestPlaceList();
        EXPECTED_PLACES_COUNT = placeList.size();
        initMockedClosestService(placeList);

        List<TrendsQueryResult> trendsQueryResults = getMockedTrendsQueryResults();
        initMockedCustomApiClient(trendsQueryResults);

        LiveData<Resource<List<TrendEnt>>> data = repository.getAllTrendsByLocation(0,
                                                                                    0,
                                                                                    0);
        verify(trendsDao).getAllTrends();
        verifyNoMoreInteractions(trendsDao);

        Observer<Resource<List<TrendEnt>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(closestApiCallback);
        verifyNoMoreInteractions(placesApiCallback);

        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<List<TrendEnt>> updatedDbData = new MutableLiveData<>();
        when(trendsDao.getAllTrends()).thenReturn(updatedDbData);

        dbData.postValue(null);
        verify(closestCall, times(1)).enqueue(closestApiCallback);
        verify(placesCall, times(EXPECTED_PLACES_COUNT)).enqueue(placesApiCallback);

        ArgumentCaptor<List<TrendEnt>> trendEntListCaptor = ArgumentCaptor.forClass(List.class);
        verify(trendsDao).insertTrendEntList(trendEntListCaptor.capture());

        List<TrendEnt> trendEnts = trendEntListCaptor.getValue();

        int expectedTotalTrends = EXPECTED_TRENDS_COUNT * EXPECTED_TRENDS_QUERY_COUNT;
        assertThat(trendEnts).hasSize(expectedTotalTrends);
        trendEnts.stream()
                .forEach(trendEnt -> {
                    // wip assert data
                    //assertThat(tweetEnt.id).isEqualTo(tweetList.get((int) tweetEnt.id).id);
                });

        List<TrendEnt> updateTrendEnts = getMockedTrendEnts();
        updatedDbData.postValue(updateTrendEnts);

        ArgumentCaptor<Resource<List<TrendEnt>>> updatedTrendCaptor =
                ArgumentCaptor.forClass(Resource.class);
        verify(observer, times(2)).onChanged(updatedTrendCaptor.capture());

        Resource<List<TrendEnt>> trendResource = updatedTrendCaptor.getValue();
        assertThat(trendResource.status).isEqualTo(Resource.SUCCESS);
        assertThat(trendResource.data).hasSize(3);
        assertThat(trendResource.data.get(0).name).isEqualTo(EXPECTED_TREND_ENT_NAME);
    }

    @Test
    public void loadTrendsFromDatabase() {
        MutableLiveData<List<TrendEnt>> dbData = new MutableLiveData<>();
        when(trendsDao.getAllTrends()).thenReturn(dbData);

        List<Place> placeList = getMockedClosestPlaceList();
        initMockedClosestService(placeList);

        List<TrendsQueryResult> trendsQueryResults = getMockedTrendsQueryResults();
        initMockedCustomApiClient(trendsQueryResults);

        when(rateLimitsRepository.shouldFetch(anyString(),
                                              anyDouble(),
                                              anyDouble(),
                                              anyLong())).thenReturn(false);

        LiveData<Resource<List<TrendEnt>>> data = repository.getAllTrendsByLocation(0,
                                                                                    0,
                                                                                    0);
        verify(trendsDao).getAllTrends();
        verifyNoMoreInteractions(trendsDao);

        Observer<Resource<List<TrendEnt>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(closestApiCallback);
        verifyNoMoreInteractions(placesApiCallback);

        verify(observer).onChanged(Resource.loading(null));

        List<TrendEnt> updateTrendEnts = getMockedTrendEnts();
        dbData.postValue(updateTrendEnts);

        verify(closestCall, times(0)).enqueue(closestApiCallback);
        verify(placesCall, times(0)).enqueue(placesApiCallback);

        ArgumentCaptor<Resource<List<TrendEnt>>> updatedTrendCaptor =
                ArgumentCaptor.forClass(Resource.class);
        verify(observer, times(2)).onChanged(updatedTrendCaptor.capture());

        Resource<List<TrendEnt>> trendResource = updatedTrendCaptor.getValue();
        assertThat(trendResource.status).isEqualTo(Resource.SUCCESS);
        assertThat(trendResource.data).hasSize(3);
        assertThat(trendResource.data.get(0).name).isEqualTo(EXPECTED_TREND_ENT_NAME);
    }

    private void initMockedClosestService(List<Place> places) {
        closestCall = mock(Call.class);

        when(customApiClient.getClosestPlacesByLocationService())
                .thenReturn(closestPlacesByLocationService);
        when(closestPlacesByLocationService
                     .closest(anyDouble(),
                              anyDouble())).thenReturn(closestCall);

        when(closestApiCallback.getResponse()).thenReturn(getClosestSucessCall(places));
    }

    private void initMockedCustomApiClient(List<TrendsQueryResult> trendsQueryResults) {
        placesApiCallback = mock(GatheringApiCallback.class);

        when(customApiClient.getTrendsByPlaceService()).thenReturn(trendsByPlaceIdService);
        when(trendsByPlaceIdService.place(anyLong())).thenReturn(placesCall);

        when(placesApiCallbackFactory.getApiCallback(anyInt())).thenReturn(placesApiCallback);

        when(placesApiCallback.getResponse()).thenReturn(getPlaceSucessCall(trendsQueryResults));
    }


    private LiveData<ApiResponse<List<Place>>> getClosestSucessCall(List<Place> places) {
        MutableLiveData<ApiResponse<List<Place>>> liveData = new MutableLiveData<>();
        liveData.setValue(ApiResponseFactory.getApiResponse(Response.success(places)));
        return liveData;
    }

    private LiveData<ApiResponse<List<TrendsQueryResult>>> getPlaceSucessCall(List<TrendsQueryResult> trends) {
        MutableLiveData<ApiResponse<List<TrendsQueryResult>>> liveData = new MutableLiveData<>();
        liveData.setValue(ApiResponseFactory.getApiResponse(Response.success(trends)));
        return liveData;
    }


}