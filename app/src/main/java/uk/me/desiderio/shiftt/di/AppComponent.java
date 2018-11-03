package uk.me.desiderio.shiftt.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.android.AndroidInjectionModule;
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
        ActivityBindingModule.class,

})
public interface AppComponent extends AndroidInjector<ShifttApplication> {

//    @dagger.Component.Builder
//    abstract class Builder extends AndroidInjector.Builder<ShifttApplication> {  }

    @dagger.Component.Builder
    interface Builder {
        Builder appModule(AppModule appModule);
        AppComponent build();
    }
}
