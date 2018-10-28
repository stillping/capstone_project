package uk.me.desiderio.shiftt.ui.tweetlist;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Subcomponent injecting the {@link TweetListFragment}
 */

@Subcomponent
public interface TweetListFragmentSubcomponent extends AndroidInjector<TweetListFragment> {

    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<TweetListFragment> {}
}
