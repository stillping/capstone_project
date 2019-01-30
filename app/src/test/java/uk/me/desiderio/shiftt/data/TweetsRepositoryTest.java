package uk.me.desiderio.shiftt.data;

import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.SearchService;
import com.twitter.sdk.android.core.services.params.Geocode;

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
import uk.me.desiderio.shiftt.data.database.TweetsDao;
import uk.me.desiderio.shiftt.data.database.model.PlaceEnt;
import uk.me.desiderio.shiftt.data.database.model.RateLimitEnt;
import uk.me.desiderio.shiftt.data.database.model.TweetEnt;
import uk.me.desiderio.shiftt.data.network.ApiCallback;
import uk.me.desiderio.shiftt.data.network.ApiResponse;
import uk.me.desiderio.shiftt.data.network.ApiResponseFactory;
import uk.me.desiderio.shiftt.data.network.ShifttTwitterApiClient;
import uk.me.desiderio.shiftt.data.repository.RateLimitsRepository;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.data.repository.TweetsRepository;
import uk.me.desiderio.shiftt.data.util.InstantAppExecutors;
import uk.me.desiderio.shiftt.ui.model.MapItem;
import uk.me.desiderio.shiftt.util.AppExecutors;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.me.desiderio.shiftt.data.mock.DataProvider.EXPECTED_MAP_ENT_NAME;
import static uk.me.desiderio.shiftt.data.mock.DataProvider.getMockedPlaceEntList;
import static uk.me.desiderio.shiftt.data.mock.DataProvider.getMockedTweetList;

/**
 * Tests for {@link TweetsRepository}
 */
@RunWith(MockitoJUnitRunner.class)
public class TweetsRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    public Call<Search> searchCall;

    @Mock
    private TweetsDao tweetsDao;

    @Mock
    private ShifttTwitterApiClient twitterApiClient;

    @Mock
    private SearchService searchService;

    @Mock
    private ApiCallback<Search> searchApiCallback;

    @Mock
    private RateLimitsRepository rateLimitsRepository;

    @Mock
    private ConnectivityLiveData connectivityLiveData;

    private TweetsRepository repository;

    @Before
    public void setUp() {
        AppExecutors appExecutors = InstantAppExecutors.getInstance();

        when(connectivityLiveData.getValue()).thenReturn(true);

        repository = new TweetsRepository(tweetsDao,
                                          appExecutors,
                                          twitterApiClient,
                                          searchApiCallback,
                                          rateLimitsRepository,
                                          connectivityLiveData);
    }

    @Test
    public void loadMapItemsFromNetwork() {
        MutableLiveData<List<PlaceEnt>> dbData = new MutableLiveData<>();
        when(tweetsDao.getAllPlaces()).thenReturn(dbData);

        List<Tweet> tweetList = getMockedTweetList();
        initMockedTwitterApiClient(tweetList);

        LiveData<Resource<List<MapItem>>> data = repository.getMapsItems("anyString",
                                                                         0,
                                                                         0,
                                                                         0,
                                                                         "9",
                                                                         "km");

        verify(tweetsDao).getAllPlaces();
        verifyNoMoreInteractions(tweetsDao);

        Observer<Resource<List<MapItem>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(searchService);

        verify(observer).onChanged(Resource.loading(null));
        MutableLiveData<List<PlaceEnt>> updatedDbData = new MutableLiveData<>();
        when(tweetsDao.getAllPlaces()).thenReturn(updatedDbData);

        dbData.postValue(null);
        verify(searchCall, times(1)).enqueue(searchApiCallback);
        ArgumentCaptor<List<TweetEnt>> tweetEntListCaptor = ArgumentCaptor.forClass(List.class);
        verify(tweetsDao).insertTweetEntities(tweetEntListCaptor.capture());

        assertThat(tweetEntListCaptor.getValue()).hasSize(10);
        tweetEntListCaptor.getValue().stream()
                .forEach(tweetEnt -> {
                    assertThat(tweetEnt.id).isEqualTo(tweetList.get((int) tweetEnt.id).id);
                });

        List<PlaceEnt> placeEnts = getMockedPlaceEntList();

        updatedDbData.postValue(placeEnts);
        ArgumentCaptor<Resource<List<MapItem>>> mapItemsCaptor =
                ArgumentCaptor.forClass(Resource.class);
        verify(observer, times(2)).onChanged(mapItemsCaptor.capture());

        Resource<List<MapItem>> mapResource = mapItemsCaptor.getValue();
        assertThat(mapResource.status).isEqualTo(Resource.SUCCESS);
        assertThat(mapResource.data).hasSize(1);
        assertThat(mapResource.data.get(0).name).isEqualTo(EXPECTED_MAP_ENT_NAME);
    }


    @Test
    public void loadMapItemsFromDatabase() {
        MutableLiveData<List<PlaceEnt>> dbData = new MutableLiveData<>();
        when(tweetsDao.getAllPlaces()).thenReturn(dbData);

        when(rateLimitsRepository.shouldFetch(anyString(),
                                              anyDouble(),
                                              anyDouble(),
                                              anyLong())).thenReturn(false);

        LiveData<Resource<List<MapItem>>> data = repository.getMapsItems("anyString",
                                                                         0,
                                                                         0,
                                                                         0,
                                                                         "9",
                                                                         "km");
        verify(tweetsDao).getAllPlaces();
        verifyNoMoreInteractions(tweetsDao);

        Observer<Resource<List<MapItem>>> observer = mock(Observer.class);
        data.observeForever(observer);
        verifyNoMoreInteractions(searchService);

        verify(observer).onChanged(Resource.loading(null));

        List<PlaceEnt> placeEnts = getMockedPlaceEntList();
        dbData.postValue(placeEnts);

        verify(searchCall, times(0)).enqueue(searchApiCallback);

        ArgumentCaptor<Resource<List<MapItem>>> mapItemsCaptor =
                ArgumentCaptor.forClass(Resource.class);
        verify(observer, times(2)).onChanged(mapItemsCaptor.capture());

        Resource<List<MapItem>> mapResource = mapItemsCaptor.getValue();
        assertThat(mapResource.status).isEqualTo(Resource.SUCCESS);
        assertThat(mapResource.data).hasSize(1);
        assertThat(mapResource.data.get(0).name).isEqualTo(EXPECTED_MAP_ENT_NAME);
    }


    private LiveData<List<RateLimitEnt>> getMockedRateLimits() {
        MutableLiveData<List<RateLimitEnt>> liveData = new MutableLiveData<>();
        return liveData;
    }

    private LiveData<ApiResponse<Search>> getSearchSucessCall(Search search) {
        MutableLiveData<ApiResponse<Search>> liveData = new MutableLiveData<>();
        liveData.setValue(ApiResponseFactory.getApiResponse(Response.success(search)));
        return liveData;
    }

    private void initMockedTwitterApiClient(List<Tweet> tweetList) {


        when(twitterApiClient.getSearchService()).thenReturn(searchService);
        when(searchService.tweets(eq("anyString"),
                                  any(Geocode.class),
                                  isNull(),
                                  isNull(),
                                  isNull(),
                                  eq(100),
                                  isNull(),
                                  isNull(),
                                  isNull(),
                                  eq(true))).thenReturn(searchCall);

        Search search = new Search(tweetList, null);
        when(searchApiCallback.getResponse()).thenReturn(getSearchSucessCall(search));
    }

}