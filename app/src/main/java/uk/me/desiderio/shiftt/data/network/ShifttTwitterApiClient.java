package uk.me.desiderio.shiftt.data.network;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

/**
 * subclass of the {@link TwitterApiClient} to be used as Retrofit Api Client
 */
public class ShifttTwitterApiClient extends TwitterApiClient {

    public ShifttTwitterApiClient() {
        super();
    }
    public ShifttTwitterApiClient(TwitterSession session) {
        super(session);
    }

    public ClosestPlacesByLocationService getClosestPlacesByLocationService() {
        return getService(ClosestPlacesByLocationService.class);
    }


    public TrendsByPlaceIdService getTrendsByPlaceService() {
        return getService(TrendsByPlaceIdService.class);
    }
}
