package uk.me.desiderio.shiftt.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import uk.me.desiderio.shiftt.TrendsListActivity;
import uk.me.desiderio.shiftt.TweetListActivity;
import uk.me.desiderio.shiftt.MainActivity;
import uk.me.desiderio.shiftt.ui.main.MainActivityModule;
import uk.me.desiderio.shiftt.ui.map.ShifftMapFragmentBindingModule;
import uk.me.desiderio.shiftt.ui.trendslist.TrendsListFragmentBindingModule;
import uk.me.desiderio.shiftt.ui.tweetlist.TweetListFragmentBindingModule;

/**
 * Module to provide bindings for the application's activities
 */

@Module
public abstract class ActivityBindingModule {

    @ActivityScope
    @ContributesAndroidInjector(modules = { MainActivityModule.class,
            ShifftMapFragmentBindingModule.class,
            TrendsListFragmentBindingModule.class,
            TweetListFragmentBindingModule.class})
    abstract MainActivity bindMainActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TrendsListFragmentBindingModule.class})
    abstract TrendsListActivity bindTrendListActivity();

    @ActivityScope
    @ContributesAndroidInjector(modules = {TweetListFragmentBindingModule.class})
    abstract TweetListActivity bindTweetListActivity();

}
