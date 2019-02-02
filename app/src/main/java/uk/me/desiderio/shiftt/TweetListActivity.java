package uk.me.desiderio.shiftt;

import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import uk.me.desiderio.shiftt.ui.tweetlist.TweetListFragment;
import uk.me.desiderio.shiftt.util.SnackbarDelegate;

/**
 * activity to show list of tweets.
 */

public class TweetListActivity extends NetworkStateResourceActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.base_fragment_container, TweetListFragment.newInstance(bundle))
                    .commitNow();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected SnackbarDelegate initSnackbarDelegate() {
        View rootView = findViewById(android.R.id.content);
        return new SnackbarDelegate(R.string.snackbar_connected_message_tweets_suffix,
                                    rootView);
    }

}
