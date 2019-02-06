package uk.me.desiderio.shiftt;

import android.os.Bundle;
import android.view.View;

import dagger.android.support.HasSupportFragmentInjector;
import uk.me.desiderio.shiftt.ui.trendslist.TrendsListFragment;
import uk.me.desiderio.shiftt.util.SnackbarDelegate;

/**
 * activity to show list of twitter trends.
 */

public class TrendsListActivity extends NetworkStateResourceActivity implements HasSupportFragmentInjector {

    public static final int REQUEST_CODE_TRENDS = 756;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (isTwoPane) {
            setResult(RESULT_OK, null);
            finish();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.base_fragment_container, TrendsListFragment.newInstance())
                    .commitNow();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected SnackbarDelegate initSnackbarDelegate() {
        View rootView = findViewById(android.R.id.content);
        return new SnackbarDelegate(this, R.string.snackbar_connected_message_trends_suffix,
                                    rootView);
    }

}
