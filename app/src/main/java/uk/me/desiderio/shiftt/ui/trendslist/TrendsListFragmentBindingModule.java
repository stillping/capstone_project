package uk.me.desiderio.shiftt.ui.trendslist;

import androidx.fragment.app.Fragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Module providing binding for {@link TrendsListFragment}
 */

@Module(subcomponents = TrendsListFragmentSubcomponent.class)
public abstract class TrendsListFragmentBindingModule {

    private TrendsListFragmentBindingModule() {
    }

    @Binds
    @IntoMap
    @FragmentKey(TrendsListFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindAndroidInjectorFactory(
            TrendsListFragmentSubcomponent.Builder builder);

}
