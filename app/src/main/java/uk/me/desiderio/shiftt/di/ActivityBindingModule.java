package uk.me.desiderio.shiftt.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import uk.me.desiderio.shiftt.NeighbourhoodActivity;
import uk.me.desiderio.shiftt.TrendsListActivity;
import uk.me.desiderio.shiftt.ui.main.MainActivity;
import uk.me.desiderio.shiftt.ui.main.MainActivityModule;
import uk.me.desiderio.shiftt.ui.neighbourhood.NeighbourhoodActivityModule;
import uk.me.desiderio.shiftt.ui.neighbourhood.NeighbourhoodFragmentBindingModule;
import uk.me.desiderio.shiftt.ui.trendslist.TrendsListActivityModule;
import uk.me.desiderio.shiftt.ui.trendslist.TrendsListFragmentBindingModule;

/**
 * Module to provide bindings for the application's activities
 */

@Module
public abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity bindMainActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = {NeighbourhoodActivityModule.class,
            NeighbourhoodFragmentBindingModule.class})
    abstract NeighbourhoodActivity bindNeighbourhoodActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TrendsListActivityModule.class,
            TrendsListFragmentBindingModule.class})
    abstract TrendsListActivity bindTrendListActivity();

}
