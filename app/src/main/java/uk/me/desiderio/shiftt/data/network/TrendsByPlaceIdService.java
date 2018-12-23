package uk.me.desiderio.shiftt.data.network;

import java.util.List;

import androidx.lifecycle.LiveData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import uk.me.desiderio.shiftt.data.network.model.TrendsQueryResult;

/**
 * Returns service to request list of {@link TrendsQueryResult} objects
 * representing trends around the location provided as a WOE id
 * <p>
 * request end point : https://api.twitter.com/1.1/trends/place.json
 * https://api.twitter.com/1.1/trends/place.json?id=1
 */
public interface TrendsByPlaceIdService {
    @GET("/1.1/trends/place.json")
    Call<List<TrendsQueryResult>> place(@Query("id") long id);
}
