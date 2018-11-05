package uk.me.desiderio.shiftt.ui.neighbourhood;

import androidx.fragment.app.Fragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Module providing binding for {@link ShifttMapFragment}
 */

@Module(subcomponents = NeighbourhoodFragmentSubcomponent.class)
public abstract class NeighbourhoodFragmentBindingModule {


    private NeighbourhoodFragmentBindingModule() {
    }

    @Binds
    @IntoMap
    @FragmentKey(ShifttMapFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindAndroidInjectorFactory(
            NeighbourhoodFragmentSubcomponent.Builder builder);
}
