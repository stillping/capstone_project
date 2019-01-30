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
        AppModule.class,
        ActivityBindingModule.class
})
public interface AppComponent extends AndroidInjector<ShifttApplication> {

    @dagger.Component.Builder
    interface Builder {
        Builder appModule(AppModule appModule);

        AppComponent build();
    }
}
