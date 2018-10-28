package uk.me.desiderio.shiftt.ui.trendslist;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Subcomponent injecting the {@link TrendsListFragment}
 */

@Subcomponent
public interface TrendsListFragmentSubcomponent extends AndroidInjector<TrendsListFragment>{

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<TrendsListFragment> {}
}

