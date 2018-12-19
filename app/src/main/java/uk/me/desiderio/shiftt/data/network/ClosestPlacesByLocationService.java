package uk.me.desiderio.shiftt.data.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import uk.me.desiderio.shiftt.data.network.model.Place;

/**
 * Returns service to request list of {@link Place} objects representing
 * coordinates close to the position provided as parameters
 * <p>
 * request end point : https://api.twitter.com/1.1/trends/closest.json
 */
public interface ClosestPlacesByLocationService {
    @GET("/1.1/trends/closest.json")
    Call<List<Place>> closest(@Query("lat") double lat, @Query("long") double lon);
}
