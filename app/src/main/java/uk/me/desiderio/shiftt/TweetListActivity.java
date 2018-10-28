package uk.me.desiderio.shiftt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import uk.me.desiderio.shiftt.ui.tweetlist.TweetListFragment;

/**
 *  activity to show list of tweets.
 */

public class TweetListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tweet_list_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, TweetListFragment.newInstance())
                    .commitNow();
        }
    }
}
