package uk.me.desiderio.shiftt.di;

import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Search;

import java.util.List;

import androidx.annotation.Nullable;
import dagger.Module;
import dagger.Provides;
import uk.me.desiderio.shiftt.data.network.ApiCallback;
import uk.me.desiderio.shiftt.data.network.GatheringApiCallback;
import uk.me.desiderio.shiftt.data.network.ShifttTwitterApiClient;
import uk.me.desiderio.shiftt.data.network.model.Place;
import uk.me.desiderio.shiftt.data.network.model.TrendsQueryResult;

@Module
public class TwitterCoreModule {

    @Provides
    ShifttTwitterApiClient providesShifttTwitterApiClient(@Nullable TwitterSession activeSession) {
        // pass custom OkHttpClient into TwitterApiClient and add to TwitterCore
        final ShifttTwitterApiClient customApiClient;
        if (activeSession != null) {
            customApiClient = new ShifttTwitterApiClient(activeSession);
            TwitterCore.getInstance().addApiClient(activeSession, customApiClient);
        } else {
            customApiClient = new ShifttTwitterApiClient();
            TwitterCore.getInstance().addGuestApiClient(customApiClient);
        }

        return customApiClient;
    }

    @Provides @Nullable
    TwitterSession providesTwitterSession() {
        return TwitterCore.getInstance().getSessionManager().getActiveSession();
    }

    @Provides
    ApiCallback<Search> providesSearchApiCallback() {
        return new ApiCallback<>();
    }

    @Provides
    ApiCallback<List<Place>> providesClosestApiCallback() {
        return new ApiCallback<>();
    }


    @Provides
    GatheringApiCallback.Factory<List<TrendsQueryResult>> providesGatheringApiCallbackFactory() {
        return new GatheringApiCallback.Factory<>();

    }
}
