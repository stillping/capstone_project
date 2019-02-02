package uk.me.desiderio.shiftt.viewmodel;

import javax.inject.Singleton;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import uk.me.desiderio.shiftt.di.ViewModelKey;
import uk.me.desiderio.shiftt.ui.main.MainActivityViewModel;
import uk.me.desiderio.shiftt.ui.map.MapDataViewModel;
import uk.me.desiderio.shiftt.ui.trendslist.TrendsListViewModel;
import uk.me.desiderio.shiftt.ui.tweetlist.TweetListViewModel;

/**
 * Module to provide {@link ViewModel} to Dagger
 */
@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(TrendsListViewModel.class)
    abstract ViewModel providesTrendsListViewModel(TrendsListViewModel trendsListViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MapDataViewModel.class)
    abstract ViewModel providesMapDataViewModel(MapDataViewModel mapDataViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel.class)
    abstract ViewModel providesMainViewModel(MainActivityViewModel mainActivityViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TweetListViewModel.class)
    abstract ViewModel providesTweetListViewModel(TweetListViewModel tweetListViewModel);

    @Binds
    @Singleton
    abstract ViewModelProvider.NewInstanceFactory bindsViewModelFactory(ViewModelFactory viewModelFactory);

}
