package uk.me.desiderio.shiftt;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import uk.me.desiderio.shiftt.ui.tweetlist.TweetListFragment;
import uk.me.desiderio.shiftt.util.SnackbarDelegate;

/**
 * activity to show list of tweets.
 */

public class TweetListActivity extends NetworkStateResourceActivity {

    public static final int REQUEST_CODE_TWEETS = 876;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isTwoPane) {
            finishWithResult();
        }

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.base_fragment_container, TweetListFragment.newInstance(bundle))
                    .commitNow();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishWithResult();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finishWithResult();
        super.onBackPressed();
    }

    @Override
    protected SnackbarDelegate initSnackbarDelegate() {
        View rootView = findViewById(android.R.id.content);
        return new SnackbarDelegate(this, R.string.snackbar_connected_message_tweets_suffix,
                                    rootView);
    }

    private void finishWithResult() {
        setResult(RESULT_OK, getIntent());
        finish();
    }

}
