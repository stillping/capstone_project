package uk.me.desiderio.shiftt.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import uk.me.desiderio.shiftt.data.network.settings.RemoteSettingIntentService;

/**
 * Module to provide bindings for the application's services
 */

@Module
public abstract class ServiceBindingModule {

    @ContributesAndroidInjector
    abstract RemoteSettingIntentService bindRemoteSettingIntentService();
}
