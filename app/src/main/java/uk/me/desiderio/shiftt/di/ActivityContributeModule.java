package uk.me.desiderio.shiftt.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import uk.me.desiderio.shiftt.ui.main.MainActivity;
import uk.me.desiderio.shiftt.ui.main.MainActivityModule;

/**
 * Module to provide bindings for the app Activities
 */

@Module
public abstract class ActivityContributeModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity bindMainActivity();

}
