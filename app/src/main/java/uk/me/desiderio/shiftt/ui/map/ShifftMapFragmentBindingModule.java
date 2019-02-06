package uk.me.desiderio.shiftt.ui.map;

import androidx.fragment.app.Fragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Module providing binding for {@link ShifttMapFragment}
 */

@Module(subcomponents = ShifttMapFragmentSubcomponent.class)
public abstract class ShifftMapFragmentBindingModule {


    private ShifftMapFragmentBindingModule() {
    }

    @Binds
    @IntoMap
    @FragmentKey(ShifttMapFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindAndroidInjectorFactory(
            ShifttMapFragmentSubcomponent.Builder builder);
}
