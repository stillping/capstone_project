package uk.me.desiderio.shiftt.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.AndroidInjection;
import uk.me.desiderio.fabmenu.FloatingActionMenu;
import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.TrendsListActivity;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

public class MainActivity extends AppCompatActivity implements
        FloatingActionMenu.OnItemClickListener {

    private MainActivityViewModel viewModel;

    ViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textView = (TextView) findViewById(R.id.main_content_text_view);

        FloatingActionMenu floatingActionMenu = findViewById(R.id.fab_menu);
        floatingActionMenu.setOnItemClickListener(this);

       viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainActivityViewModel.class);
        // TODO: Use the ViewModel
       viewModel.getMessage().observe(this, message -> {
            textView.setText(message);
        });

        viewModel.getMessage().setValue("Hello World, Winter is coming");
    }

    @Override
    public void onFloatingMenuItemClick(View v) {
        int viewId = v.getId();
        String message = " NO message";

        Intent intent = new Intent(this, TrendsListActivity.class);
        startActivity(intent);

        switch (viewId) {
            case R.id.fab_trends:
                message = "trends clicked";

                break;
            case R.id.fab_neighbourhood:
                message = "neighbour clicked";
                break;
            default:
        }

        Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
