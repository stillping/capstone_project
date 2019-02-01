package uk.me.desiderio.shiftt;

import android.os.Bundle;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import uk.me.desiderio.shiftt.ui.tweetlist.TweetListFragment;

/**
 * activity to show list of tweets.
 */

public class TweetListActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> fragmentInjector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweet_list_activity);

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TweetListFragment.newInstance(bundle))
                    .commitNow();
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return fragmentInjector;
    }
}
