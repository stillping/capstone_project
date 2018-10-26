package uk.me.desiderio.shiftt.viewmodel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import uk.me.desiderio.shiftt.di.ViewModelKey;
import uk.me.desiderio.shiftt.ui.main.MainActivityViewModel;

/**
 * Module to provide {@link ViewModel} to Dagger
 */
@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel.class)
    abstract ViewModel providesMainViewModel(MainActivityViewModel mainActivityViewModel);

    @Binds
    abstract ViewModelFactory bindsViewModelFactory(ViewModelFactory viewModelFactory);
}
