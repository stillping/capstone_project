package uk.me.desiderio.shiftt.ui.neighbourhood;


import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Subcomponent injecting the {@link ShifttMapFragment}
 */

@Subcomponent
public interface NeighbourhoodFragmentSubcomponent extends AndroidInjector<ShifttMapFragment> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<ShifttMapFragment> {}
}
