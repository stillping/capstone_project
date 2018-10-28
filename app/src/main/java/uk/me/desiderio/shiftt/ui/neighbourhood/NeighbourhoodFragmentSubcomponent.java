package uk.me.desiderio.shiftt.ui.neighbourhood;


import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Subcomponent injecting the {@link NeighbourhoodFragment}
 */

@Subcomponent
public interface NeighbourhoodFragmentSubcomponent extends AndroidInjector<NeighbourhoodFragment> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<NeighbourhoodFragment> {}
}
