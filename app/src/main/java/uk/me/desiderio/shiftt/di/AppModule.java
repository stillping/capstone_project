package uk.me.desiderio.shiftt.di;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import uk.me.desiderio.shiftt.data.database.ShifttDatabase;
import uk.me.desiderio.shiftt.data.database.TrendsDao;
import uk.me.desiderio.shiftt.data.database.TweetsDao;
import uk.me.desiderio.shiftt.utils.AppExecutors;
import uk.me.desiderio.shiftt.viewmodel.ViewModelModule;

/**
 * Module to provided bindings at application level
 */

@Module(includes = {ViewModelModule.class,
        AppBindingsModule.class,
        TwitterCoreModule.class})
public class AppModule {

    Application app;

    public AppModule(Application app) {
        this.app = app;
    }

    @Provides
    @ForApplication
    Application providesApplication() {
        return app;
    }

    @Provides
    @ForApplication
    Context providesContext() {
        return app;
    }

    @Provides
    LocationManager providesLocationManager(@ForApplication Application context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    FusedLocationProviderClient providesFusedLocationProviderClient(@ForApplication Context context) {
        return LocationServices.getFusedLocationProviderClient(context);
    }

    @Provides
    AppExecutors providesAppExecutors() {
        return AppExecutors.getInstance();
    }

    @Provides
    ShifttDatabase providesDatabase(@ForApplication Context context) {
        return Room.databaseBuilder(context,
                                    ShifttDatabase.class,
                                    ShifttDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    TweetsDao providesTweetsDao(ShifttDatabase database) {
        return database.tweetsDao();
    }

    @Provides
    TrendsDao provideTrendssDao(ShifttDatabase database) {
        return database.trendsDao();
    }

}
