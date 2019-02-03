package uk.me.desiderio.shiftt;

import com.twitter.sdk.android.core.Twitter;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import uk.me.desiderio.shiftt.di.AppComponent;
import uk.me.desiderio.shiftt.di.AppModule;
import uk.me.desiderio.shiftt.di.DaggerAppComponent;

/**
 * Application class to initialize Dagger by building {@link AppComponent} instance and injecting
 * this object into the map.
 */

public class ShifttApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Twitter.initialize(this);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }
}
