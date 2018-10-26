package uk.me.desiderio.shiftt;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import uk.me.desiderio.shiftt.di.AppComponent;
import uk.me.desiderio.shiftt.di.DaggerAppComponent;


/**
 * Application class to initialize Dagger by building {@link AppComponent} instance and injecting
 * this object into the map.
 */
public class ShifttApplication extends DaggerApplication {
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().create(this);
    }
}
