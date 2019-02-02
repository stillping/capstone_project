package uk.me.desiderio.shiftt.ui.map;


import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Subcomponent injecting the {@link ShifttMapFragment}
 */

@Subcomponent
public interface ShifttMapFragmentSubcomponent extends AndroidInjector<ShifttMapFragment> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<ShifttMapFragment> {}
}
