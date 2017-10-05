package tm.fantom.doittesttask;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

/**
 * Created by fantom on 29-Sep-17.
 */

public class DoItApp extends Application {

    private DoComponent doComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        doComponent = DaggerDoComponent.builder()
                .doModule(new DoModule(this))
                .apiModule(new ApiModule())
                .build();
    }

    public static DoComponent getComponent(Context context) {
        return ((DoItApp) context.getApplicationContext()).doComponent;
    }

}
