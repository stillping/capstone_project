package uk.me.desiderio.shiftt.ui.tweetlist;

import androidx.fragment.app.Fragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Module providing binding for {@link TweetListFragment}
 */

@Module(subcomponents = TweetListFragmentSubcomponent.class)
public abstract class TweetListFragmentBindingModule {

    private TweetListFragmentBindingModule() {
    }

    @Binds
    @IntoMap
    @FragmentKey(TweetListFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> bindAndroidInjectorFactory(
            TweetListFragmentSubcomponent.Builder builder);
}
