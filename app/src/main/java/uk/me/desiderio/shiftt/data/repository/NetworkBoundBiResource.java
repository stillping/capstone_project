package uk.me.desiderio.shiftt.data.repository;

import androidx.annotation.MainThread;
import androidx.lifecycle.LiveData;
import uk.me.desiderio.shiftt.data.network.ApiResponse;
import uk.me.desiderio.shiftt.data.network.ApiSuccessResponse;
import uk.me.desiderio.shiftt.util.AppExecutors;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;

/**
 * Provides a {@link Resource} with data gathered from both database and Twitter API. As well as
 * data, it provides info on the process state.
 * <p>
 * The class is an extension of {@link NetworkBoundResource}. It implements a different strategy
 * when retrieving data from network. It solves the problem when the data to be provided it is
 * dependendant on TWO consecutive network request.
 */
public abstract class NetworkBoundBiResource<InitialRequestType, NestedRequestType, ResultType>
        extends NetworkBoundResource<NestedRequestType, ResultType> {

    public NetworkBoundBiResource(AppExecutors executors,
                                  ConnectivityLiveData connectivityLiveData) {
        super(executors, connectivityLiveData);
    }

    protected void fetchFromNetwork(LiveData<ResultType> dbSource) {
        LiveData<ApiResponse<InitialRequestType>> apiResponse = createInitialCall();
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource, newData ->
                setValue(Resource.loading(newData)));

        result.addSource(apiResponse, response -> {
            result.removeSource(apiResponse);
            result.removeSource(dbSource);

            processApiResponse(response, dbSource);
        });
    }

    @SuppressWarnings("unchecked")
    private void processApiResponse(ApiResponse<InitialRequestType>
                                            initialResponse,
                                    LiveData<ResultType> dbSource) {

        if (initialResponse instanceof ApiSuccessResponse) {
            appExecutors.getDiskIO().execute(() -> {
                ApiSuccessResponse<InitialRequestType> initialSuccessResponse =
                        (ApiSuccessResponse) initialResponse;
                LiveData<ApiResponse<NestedRequestType>> apiResponse = createCall(initialSuccessResponse.body);

                appExecutors.getMainThread().execute(() -> {
                    result.addSource(apiResponse, nestedRequestTypeApiResponse -> {
                        result.removeSource(apiResponse);


                        if (nestedRequestTypeApiResponse instanceof ApiSuccessResponse) {

                            ApiSuccessResponse<NestedRequestType> successResponse = (ApiSuccessResponse) nestedRequestTypeApiResponse;
                            processFinalSuccessResponse(successResponse);

                        } else {
                            processApiResponseAsNonSuccess(nestedRequestTypeApiResponse, dbSource);
                        }
                    });
                });
            });

        } else {
            processApiResponseAsNonSuccess(initialResponse, dbSource);
        }
    }

    @MainThread
    protected abstract LiveData<ApiResponse<InitialRequestType>> createInitialCall();

    @MainThread
    protected abstract LiveData<ApiResponse<NestedRequestType>> createCall(InitialRequestType initialSuccessResponse);


    @Override
    protected LiveData<ApiResponse<NestedRequestType>> createCall() {
        throw new UnsupportedOperationException("createCall");
    }
}

