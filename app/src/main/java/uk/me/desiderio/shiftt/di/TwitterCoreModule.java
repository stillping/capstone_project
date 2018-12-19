package uk.me.desiderio.shiftt.di;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import androidx.annotation.Nullable;
import dagger.Module;
import dagger.Provides;
import uk.me.desiderio.shiftt.data.network.ShifttTwitterApiClient;

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
    TwitterApiClient providesTwitterApiClient() {
        return TwitterCore.getInstance().getApiClient();
    }
}
