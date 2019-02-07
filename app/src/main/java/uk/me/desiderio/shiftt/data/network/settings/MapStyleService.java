package uk.me.desiderio.shiftt.data.network.settings;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Retrofit service to load app setting from server
 */
public interface MapStyleService {
    @GET("map_style.json")
    Call<ResponseBody> mapStyle();
}
