package tm.fantom.doittesttask;

import android.content.ContentResolver;

import javax.inject.Singleton;

import dagger.Component;
import tm.fantom.doittesttask.ui.GifFragment;
import tm.fantom.doittesttask.ui.LoginFragment;
import tm.fantom.doittesttask.ui.MainFragment;
import tm.fantom.doittesttask.ui.SplashActivity;
import tm.fantom.doittesttask.ui.UploadFragment;

/**
 * Created by fantom on 29-Sep-17.
 */
@Singleton
@Component(modules = {DoModule.class, ApiModule.class})
public interface DoComponent {
    void inject(LoginFragment fragment);
    void inject(SplashActivity activity);
    void inject(MainFragment fragment);
    void inject(UploadFragment fragment);
    void inject(GifFragment fragment);
}
