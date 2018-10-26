package uk.me.desiderio.shiftt.di;

import javax.inject.Singleton;

import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import uk.me.desiderio.shiftt.ShifttApplication;

/**
 * Dagger application component
 */

@Singleton
@dagger.Component(modules = {
        AndroidSupportInjectionModule.class,
        ActivityContributeModule.class,
        AppModule.class
})
public interface AppComponent extends AndroidInjector<ShifttApplication> {

    @dagger.Component.Builder
    abstract class Builder extends AndroidInjector.Builder<ShifttApplication> {
    }
}
