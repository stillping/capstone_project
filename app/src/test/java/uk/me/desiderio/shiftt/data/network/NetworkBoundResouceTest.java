package uk.me.desiderio.shiftt.data.network;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;
import uk.me.desiderio.shiftt.data.repository.NetworkBoundResouce;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.data.util.InstantAppExecutors;
import uk.me.desiderio.shiftt.util.AppExecutors;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link NetworkBoundResouce}
 */
@RunWith(MockitoJUnitRunner.class)
public class NetworkBoundResouceTest {

    public static final String HEADER_ELEMENT_NAME = "header_element_one_name";
    public static final String HEADER_ELEMENT_VALUE = "header_element_one_value";

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    public Observer<Resource<Foo>> observer;
    public Headers headers;
    private AppExecutors appExecutors;

    @Mock
    private ConnectivityLiveData connectivityLiveData;

    private Supplier<LiveData<ApiResponse<Foo>>> handleCreateCall = MutableLiveData::new;
    private final MutableLiveData<Foo> dbData = new MutableLiveData<>();
    private Consumer<Foo> handleSaveCallResult;
    private Consumer<Headers> handleSaveHeadersInfo;
    private Predicate<Foo> handleShouldMatch;
    private NetworkBoundResouce<Foo, Foo> networkBoundResouce;
    private final AtomicBoolean fetchedOnce = new AtomicBoolean(false);

    @Before
    public void setUp() {
        appExecutors = InstantAppExecutors.getInstance();
        headers = Headers.of(HEADER_ELEMENT_NAME, HEADER_ELEMENT_VALUE);
        when(connectivityLiveData.getValue()).thenReturn(true);
        initNetworkBoundResouce();
    }

    @Test
    public void
    givenNoDataInDB_whenNetworkRequestSuccessful_thenResourceSuccessIsReturned() {

        AtomicReference saved = new AtomicReference<Foo>();
        AtomicReference savedHeaders = new AtomicReference<Headers>();

        handleShouldMatch = foo -> foo == null;
        Foo fetchedDbValue = new Foo(1);

        handleSaveCallResult = foo -> {
            saved.set(foo);
            dbData.setValue(fetchedDbValue);
        };

        handleSaveHeadersInfo = headers -> {
            savedHeaders.set(headers);
        };

        Foo networkResult = new Foo(1);

        handleCreateCall = createApiCall(Response.success(networkResult, headers));

        networkBoundResouce.asLiveData().observeForever(observer);

        verify(observer).onChanged(Resource.loading(null));
        reset(observer);

        dbData.setValue(null);

        assertThat(saved.get()).isEqualTo(networkResult);
        assertThat(((Headers) savedHeaders.get()).get(HEADER_ELEMENT_NAME))
                .isEqualTo(HEADER_ELEMENT_VALUE);
        verify(observer).onChanged(Resource.success(fetchedDbValue));
    }

    @Test
    public void givenNoDataInDB_whenNetworkRequestError_thenResourceErrorIsReturned() {

        AtomicReference saved = new AtomicReference(false);
        AtomicReference savedHeaders = new AtomicReference<Headers>();

        handleShouldMatch = foo -> foo == null;

        handleSaveCallResult = foo -> {
            saved.set(true);
        };

        handleSaveHeadersInfo = headers -> {
            savedHeaders.set(headers);
        };

        ResponseBody body = ResponseBody.create(MediaType.parse("text/html"), "error");
        Response<Foo> response = Response.error(500, body);
        handleCreateCall = createApiCall(response);

        networkBoundResouce.asLiveData().observeForever(observer);

        verify(observer).onChanged(Resource.loading(null));
        reset(observer);

        dbData.setValue(null);

        assertThat(saved.get()).isEqualTo(false);
        assertThat(savedHeaders.get()).isNull();
        verify(observer).onChanged(Resource.error(response.errorBody().toString(), null));
    }

    @Test
    public void
    givenNoNetwork_whenDataRequest_thenDBDataisReturned() {

        when(connectivityLiveData.getValue()).thenReturn(false);

        Foo dbValueOne = new Foo(1);

        AtomicReference saved = new AtomicReference(false);
        AtomicReference savedHeaders = new AtomicReference<Headers>();

        handleShouldMatch = foo -> foo.equals(dbValueOne);

        handleSaveCallResult = foo -> {
            saved.set(true);
        };

        handleSaveHeadersInfo = headers -> {
            savedHeaders.set(headers);
        };


        networkBoundResouce.asLiveData().observeForever(observer);

        verify(observer).onChanged(Resource.loading(null));
        reset(observer);

        dbData.setValue(dbValueOne);

        verify(observer).onChanged(Resource.noConnection(dbValueOne));
        verifyNoMoreInteractions(observer);

        assertThat(saved.get()).isEqualTo(false);
        assertThat(savedHeaders.get()).isNull();
    }

    @Test
    public void
    givenFetchFailure_whenDataRequest_thenDBDataisReturned() {
        Foo dbValueOne = new Foo(1);
        AtomicReference saved = new AtomicReference(false);
        AtomicReference savedHeaders = new AtomicReference<Headers>();

        handleShouldMatch = foo -> {
            return foo.equals(dbValueOne);
        };

        handleSaveCallResult = foo -> {
            saved.set(true);
        };

        handleSaveHeadersInfo = headers -> {
            savedHeaders.set(headers);
        };

        ResponseBody body = ResponseBody.create(MediaType.parse("text/html"), "error");

        MutableLiveData<ApiResponse<Foo>> apiResponseLiveData = new MutableLiveData<>();
        handleCreateCall = () -> apiResponseLiveData;

        networkBoundResouce.asLiveData().observeForever(observer);

        verify(observer).onChanged(Resource.loading(null));
        reset(observer);


        dbData.setValue(dbValueOne);
        verify(observer).onChanged(Resource.loading(dbValueOne));

        Response errorResponse = Response.error(400, body);
        apiResponseLiveData.setValue(ApiResponseFactory.getApiResponse(errorResponse));

        assertThat(saved.get()).isEqualTo(false);
        assertThat(savedHeaders.get()).isNull();


        Foo dbResultTwo = new Foo(2);
        dbData.setValue(dbResultTwo);

        verify(observer).onChanged(Resource.error(errorResponse.errorBody().toString(), dbResultTwo));
    }


    @Test
    public void
    givenRequest_whenResponse_thenDBDataIsReturnedNewFetch() {
        Foo dbValueOne = new Foo(1);
        Foo dbValueTwo = new Foo(2);
        AtomicReference saved = new AtomicReference<Foo>();
        AtomicReference savedHeaders = new AtomicReference<Headers>();

        handleShouldMatch = foo -> {
            return foo.equals(dbValueOne);
        };

        handleSaveCallResult = foo -> {
            saved.set(foo);
            dbData.setValue(dbValueTwo);
        };

        handleSaveHeadersInfo = headers -> {
            savedHeaders.set(headers);
        };

        MutableLiveData<ApiResponse<Foo>> apiResponseLiveData = new MutableLiveData<>();
        handleCreateCall = () -> apiResponseLiveData;

        networkBoundResouce.asLiveData().observeForever(observer);

        verify(observer).onChanged(Resource.loading(null));
        reset(observer);

        dbData.setValue(dbValueOne);
        verify(observer).onChanged(Resource.loading(dbValueOne));

        Foo networkResult = new Foo(1);

        Response successResponse = Response.success(networkResult, headers);
        apiResponseLiveData.setValue(ApiResponseFactory.getApiResponse(successResponse));


        assertThat(saved.get()).isEqualTo(networkResult);
        assertThat(savedHeaders.get()).isNotNull();
        assertThat(((Headers) savedHeaders.get()).get(HEADER_ELEMENT_NAME))
                .isEqualTo(HEADER_ELEMENT_VALUE);


        verify(observer).onChanged(Resource.success(dbValueTwo));
    }

    private Supplier<LiveData<ApiResponse<Foo>>> createApiCall(Response<Foo> response) {
        MutableLiveData<ApiResponse<Foo>> liveData = new MutableLiveData<>();

        ApiResponse<Foo> apiResponse = ApiResponseFactory.getApiResponse(response);

        liveData.setValue(apiResponse);
        return () -> liveData;
    }

    private void initNetworkBoundResouce() {
        networkBoundResouce = new NetworkBoundResouce<Foo, Foo>(appExecutors,
                                                                connectivityLiveData) {
            @Override
            protected void saveHeaderInfo(Headers headers) {
                handleSaveHeadersInfo.accept(headers);
            }

            @Override
            protected void saveCallResult(Foo item) {
                handleSaveCallResult.accept(item);
            }

            @Override
            protected boolean shouldFetch(@Nullable Foo data) {
                return handleShouldMatch.test(data) && fetchedOnce.compareAndSet(false, true);
            }

            @Override
            protected LiveData<Foo> loadFromDb() {
                return dbData;
            }

            @Override
            protected LiveData<ApiResponse<Foo>> createCall() {
                return handleCreateCall.get();
            }
        };
    }

    private class Foo {
        private final int value;

        public Foo(int value) {
            this.value = value;
        }
    }
}