package uk.me.desiderio.shiftt.data.network;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Communicates responses from a server or offline requests
 *
 * It extends Retrofit's {@link Callback}, so that it returns the appropiated {@link ApiResponse}
 * depending on response or failure.
 *
 * {@link ApiResponse} is returned as a {@link LiveData}
 */
public class ApiCallback<T> implements Callback<T> {

    private final MutableLiveData<ApiResponse<T>> responseMutableLiveData;

    public ApiCallback() {
        this.responseMutableLiveData = new MutableLiveData<>();
    }

    public LiveData<ApiResponse<T>> getResponse() {
        return responseMutableLiveData;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        ApiResponse<T> apiResponse = ApiResponseFactory.getApiResponse(response);
        responseMutableLiveData.setValue(apiResponse);
    }

    @Override
    public void onFailure(Call call, Throwable t) {
        ApiResponse<T> apiResponse = ApiResponseFactory.getApiResponse(t);
        responseMutableLiveData.setValue(apiResponse);
    }
}
