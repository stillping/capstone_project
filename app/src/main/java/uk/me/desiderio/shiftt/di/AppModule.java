package uk.me.desiderio.shiftt.di;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Singleton;

import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import uk.me.desiderio.shiftt.data.database.RateLimitDao;
import uk.me.desiderio.shiftt.data.database.ShifttDatabase;
import uk.me.desiderio.shiftt.data.database.TrendsDao;
import uk.me.desiderio.shiftt.data.database.TweetsDao;
import uk.me.desiderio.shiftt.util.AppExecutors;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;
import uk.me.desiderio.shiftt.viewmodel.ViewModelModule;

/**
 * Module to provided bindings at application level
 */

@Module(includes = {ViewModelModule.class,
        AppBindingsModule.class,
        TwitterCoreModule.class})
public class AppModule {

    private final Application app;
    private ShifttDatabase database;

    public AppModule(Application app) {
        this.app = app;
        database = Room.databaseBuilder(app,
                                        ShifttDatabase.class,
                                        ShifttDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
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
    @Singleton
    LocationManager providesLocationManager(@ForApplication Application context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    @Singleton
    FusedLocationProviderClient providesFusedLocationProviderClient(@ForApplication Context context) {
        return LocationServices.getFusedLocationProviderClient(context);
    }

    @Provides
    @Singleton
    AppExecutors providesAppExecutors() {
        return AppExecutors.getInstance();
    }

    @Provides
    @Singleton
    ShifttDatabase providesDatabase() {
        return database;
    }

    @Provides
    @Singleton
    TweetsDao providesTweetsDao(ShifttDatabase database) {
        return database.tweetsDao();
    }

    @Provides
    @Singleton
    TrendsDao provideTrendssDao(ShifttDatabase database) {
        return database.trendsDao();
    }


    @Provides
    @Singleton
    RateLimitDao provideRateLimitDao(ShifttDatabase database) {
        return database.rateLimitsDao();
    }

    @Provides
    ConnectivityLiveData providesConnectivityLiveData(@ForApplication Context context) {
        return new ConnectivityLiveData(context);
    }
}
