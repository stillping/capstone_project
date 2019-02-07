package uk.me.desiderio.shiftt.di;

import android.content.Context;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import uk.me.desiderio.shiftt.data.repository.RemoteSettingsRepository;

@Module
public class RemoteSettingsModule {

    @Provides
    @Singleton
    RemoteSettingsRepository providesRemoteSettingsRepository(@ForApplication Context context) {
        return new RemoteSettingsRepository(context);
    }
}
