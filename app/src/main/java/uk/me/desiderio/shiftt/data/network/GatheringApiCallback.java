package uk.me.desiderio.shiftt.data.network;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Communicates responses from a server or offline requests
 * <p>
 * It extends Retrofit's {@link Callback}, so that it returns the appropiated {@link ApiResponse}
 * depending on response or failure.
 * <p>
 * It gathers responses of concurrent requests. The length of those request should be provided as
 * constructor's paramater. It returns when all the request have returned.
 * <p>
 * {@link ApiResponse} is returned as a {@link LiveData}
 */
public class GatheringApiCallback<T> implements Callback<T> {

    private final int requestSize;
    private final MutableLiveData<ApiResponse<T>> responseMutableLiveData;
    private final List<T> data;
    private int responseCount;

    public GatheringApiCallback(int listSize) {
        this.responseMutableLiveData = new MutableLiveData<>();
        this.data = new ArrayList<>();
        this.requestSize = listSize;
    }

    public LiveData<ApiResponse<T>> getResponse() {
        return responseMutableLiveData;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        responseCount++;
        ApiResponse<T> apiResponse = ApiResponseFactory.getApiResponse(response);
        setValue(apiResponse);
    }

    private void prepareApiResponse(ApiSuccessResponse<T> apiResponse) {
        apiResponse.body = (T) data;
    }

    private List<T> getBody(ApiSuccessResponse<T> apiResponse) {
        return (List<T>) apiResponse.body;
    }

    private void setValue(ApiResponse<T> apiResponse) {
        if (apiResponse instanceof ApiSuccessResponse) {
            ApiSuccessResponse<T> apiSuccessResponse = (ApiSuccessResponse<T>) apiResponse;
            addData(getBody(apiSuccessResponse));
            prepareApiResponse(apiSuccessResponse);
        }
        checkData(apiResponse);
    }

    private void checkData(ApiResponse<T> apiResponse) {
        if (responseCount == requestSize) {
            responseMutableLiveData.setValue(apiResponse);
        }
    }


    private void addData(List<T> items) {
        this.data.addAll(items);
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        ApiResponse<T> apiResponse = ApiResponseFactory.getApiResponse(t);
        responseMutableLiveData.setValue(apiResponse);
    }

    public static class Factory<T> {
        public GatheringApiCallback<T> getApiCallback(int listSize) {
            return new GatheringApiCallback<>(listSize);
        }
    }

}
