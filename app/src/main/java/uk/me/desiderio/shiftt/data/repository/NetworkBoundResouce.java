package uk.me.desiderio.shiftt.data.repository;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import okhttp3.Headers;
import uk.me.desiderio.shiftt.data.network.ApiEmptyResponse;
import uk.me.desiderio.shiftt.data.network.ApiErrorResponse;
import uk.me.desiderio.shiftt.data.network.ApiResponse;
import uk.me.desiderio.shiftt.data.network.ApiSuccessResponse;
import uk.me.desiderio.shiftt.util.AppExecutors;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;

/**
 * Provides a {@link Resource} with data gathered from both database and Twitter API. As well as
 *  data, it provides info on the process state.
 *
 * The class implements Template Method pattern setting logic and steps to retrieve data in a number
 * the cases.
 *
 * This class was implemented following googlesamples' android-architecture-components.
 *
 * @see <a href="https://github.com/googlesamples/android-architecture-components/blob/master/GithubBrowserSample/app/src/main/java/com/android/example/github/repository/NetworkBoundResource.kt">NetworkBoundResource.kt</a>
 *
 */
public abstract class NetworkBoundResouce<RequestType, ResultType>{

    protected final AppExecutors appExecutors;
    protected final MediatorLiveData<Resource<ResultType>> result;
    private final ConnectivityLiveData connectivityLiveData;


    public NetworkBoundResouce(AppExecutors executors,
                               ConnectivityLiveData connectivityLiveData) {
        this.appExecutors = executors;
        this.connectivityLiveData = connectivityLiveData;

        result = new MediatorLiveData<>();

        init();
    }

    private void init() {
        Resource resource = Resource.<ResultType>loading(null);
        result.setValue(resource);

        LiveData<ResultType> dbSource = loadFromDb();

        result.addSource(dbSource, data -> {
            result.removeSource(dbSource);
            if (shouldFetch(data)) {

                if(connectivityLiveData.getValue()) {
                    fetchFromNetwork(dbSource);
                } else {
                    result.addSource(dbSource, newData ->
                            setValue(Resource.noConnection(newData)));
                }
            } else {
                result.addSource(dbSource, newData ->
                        setValue(Resource.success(newData))
                );
            }
        });
    }

    @MainThread
    protected void setValue(Resource<ResultType> newValue) {
        if (result.getValue() != newValue) {
            result.setValue(newValue);
        }
    }



    protected void fetchFromNetwork(LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<RequestType>> apiResponse = createCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, newData ->
                setValue(Resource.loading(newData)));

        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);

            if (response instanceof ApiSuccessResponse) {

                    processFinalSuccessResponse((ApiSuccessResponse) response);

            } else {
                processApiResponseAsNonSuccess(response, dbSource);
            }
        });
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    protected final void processFinalSuccessResponse(ApiSuccessResponse<RequestType> response) {

        appExecutors.getDiskIO().execute(() -> {
            ApiSuccessResponse<RequestType> successResponse = response;
            saveCallResult(processResponse(successResponse));
            saveHeaderInfo(processHeader(successResponse));
            appExecutors.getMainThread().execute(() -> {
                // we specially request a new live data,
                // otherwise we will get immediately last cached value,
                // which may not be updated with latest results received from network.
                result.addSource(loadFromDb(), newData ->
                        setValue(Resource.success(newData)));
            });
        });

    }

    protected final void processApiResponseAsNonSuccess(ApiResponse response, LiveData<ResultType>
            dbSource) {
        if (response instanceof ApiEmptyResponse) {
            // wip : this execute might be irrelevant as it seems the method is called from main
            // thread
            appExecutors.getMainThread().execute(() -> {
                // reload from disk whatever we had
                result.addSource(loadFromDb(), newData ->
                        setValue(Resource.success(newData)));
            });

        } else if (response instanceof ApiErrorResponse) {
            ApiErrorResponse errorResponse = (ApiErrorResponse) response;
            onFetchFailed();
            result.addSource(dbSource, newData ->
                    setValue(Resource.error(errorResponse.message, newData)));
        }
    }

    public void onFetchFailed() {}

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected RequestType processResponse(ApiSuccessResponse<RequestType> response) {
        return response.body;
    }

    @WorkerThread
    protected Headers processHeader(ApiSuccessResponse<RequestType> response) {
        return response.headers;
    }

    @WorkerThread
    protected abstract void saveHeaderInfo(Headers headers);

    @WorkerThread
    protected abstract void saveCallResult(RequestType item);

    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @MainThread
    protected abstract LiveData<ResultType> loadFromDb();

    @MainThread
    protected abstract LiveData<ApiResponse<RequestType>> createCall();
}
