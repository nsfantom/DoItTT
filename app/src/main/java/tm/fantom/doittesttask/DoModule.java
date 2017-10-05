package tm.fantom.doittesttask;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import tm.fantom.doittesttask.util.PermissionChecker;
import tm.fantom.doittesttask.util.SessionStorage;

/**
 * Created by fantom on 26-Sep-17.
 */
@Module
public final class DoModule {
    private final Application application;

    public DoModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    SessionStorage provideSessionStorage(){
        return SessionStorage.get(provideApplication());
    }

    @Provides
    @Singleton
    PermissionChecker providePermissionChecker() {
        return new PermissionChecker(provideApplication());
    }

    @Provides
    @Singleton
    FusedLocationProviderClient provideFusedLocationProviderClient() {
        return LocationServices.getFusedLocationProviderClient(provideApplication());
    }

    @Singleton
    @Provides
    ContentResolver provideContentResolver() {
        return provideApplication().getContentResolver();
    }
}
