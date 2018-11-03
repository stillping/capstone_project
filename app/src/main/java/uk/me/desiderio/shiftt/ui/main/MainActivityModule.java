package uk.me.desiderio.shiftt.ui.main;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import uk.me.desiderio.shiftt.MainActivity;
import uk.me.desiderio.shiftt.di.ForActivity;

/**
 * Module to provide dependencies for {@link MainActivity}
 */

@Module
public abstract class MainActivityModule {
    // TODO implements provides methods

    @Binds
    @ForActivity
    abstract Activity bindsActivity(MainActivity activity);


}
