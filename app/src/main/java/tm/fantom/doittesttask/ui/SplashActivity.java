package tm.fantom.doittesttask.ui;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;
import tm.fantom.doittesttask.DoItApp;
import tm.fantom.doittesttask.R;
import tm.fantom.doittesttask.api.ApiService;
import tm.fantom.doittesttask.api.model.User;
import tm.fantom.doittesttask.util.Connectivity;
import tm.fantom.doittesttask.util.SessionStorage;

/**
 * Created by fantom on 29-Sep-17.
 */

public class SplashActivity extends AppCompatActivity implements LoginFragment.Listener {

    @Inject ApiService apiService;
    @Inject SessionStorage sessionStorage;
    @BindView(R.id.splash_title) TextView logo;
    @BindView(R.id.progressBar) ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        DoItApp.getComponent(this).inject(this);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            logo.setText(Html.fromHtml(getString(R.string.styled_splash_title),Html.FROM_HTML_MODE_LEGACY));
        else
            logo.setText(Html.fromHtml(getString(R.string.styled_splash_title)));
        if(sessionStorage.isLoggedIn()){
            new Handler().postDelayed(() -> {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }, 1000);
        } else {
            LayoutTransition lt = new LayoutTransition();
            lt.setInterpolator(LayoutTransition.APPEARING, new AccelerateInterpolator());
            lt.setDuration(300);
            ViewGroup vg = ((ViewGroup) findViewById(R.id.login_container));
            vg.setLayoutTransition(lt);
            ViewGroup v = new FrameLayout(this);
            v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            v.setId(R.id.login_holder);
            vg.addView(v);
            new Handler().postDelayed(this::initLogin, 1000);
            Timber.d("need to login");
        }
    }

    private void initLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.login_holder, LoginFragment.newInstance(), "login")
                .commitAllowingStateLoss();
    }

    @Override
    public void onLogin(String email, String password) {
        if(!isConnected()) return;
        progressBar.setVisibility(View.VISIBLE);
        apiService.login(email, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(loginResponse -> {
                    Timber.e(loginResponse.toString());
                    if (loginResponse.isSuccessful()){
                        sessionStorage.setToken(loginResponse.body().getToken());
                        startMain();
                    } else showError(loginResponse.errorBody().string());
                },e -> showError(e.getMessage()));
    }

    @Override
    public void onRegistration(User user) {
        if(user.getAvatar() == null) {
            showError(getString(R.string.error_avatar));
            return;
        }
        if(!isConnected()) return;
        progressBar.setVisibility(View.VISIBLE);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        user.getAvatar());
        apiService.createUser(
                stringToRequestBody(user.getUsername()),
                stringToRequestBody(user.getEmail()),
                stringToRequestBody(user.getPassword()),
                MultipartBody.Part.createFormData("avatar", user.getAvatar().getName(), requestFile))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(signInResponse -> {
                    Timber.e(signInResponse.toString());
                    if(signInResponse.isSuccessful()){
                        sessionStorage.setToken(signInResponse.body().getToken());
                        startMain();
                    } else {
                        showError(signInResponse.errorBody().string());
                    }
                },e -> showError(e.getMessage()));
    }

    private void startMain() {
        progressBar.setVisibility(View.GONE);
        // start main
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private RequestBody stringToRequestBody(String string) {
        return RequestBody.create(MediaType.parse("text/plain"), string);
    }

    private boolean isConnected() {
        if (!Connectivity.isConnected(this)) {
            hideKeyboard();
            Snackbar.make(logo, R.string.no_connection, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_go_online, view -> {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                        startActivity(intent);
                    })
                    .show();
            return false;
        }
        return true;
    }

    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(logo.getWindowToken(), 0);
    }

    private void showError(String errorText){
        Timber.e(errorText);
        Snackbar.make(logo,errorText,Snackbar.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }
}
