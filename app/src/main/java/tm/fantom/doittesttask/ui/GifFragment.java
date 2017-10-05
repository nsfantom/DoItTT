package tm.fantom.doittesttask.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;
import tm.fantom.doittesttask.DoItApp;
import tm.fantom.doittesttask.R;
import tm.fantom.doittesttask.api.ApiService;
import tm.fantom.doittesttask.util.Connectivity;

/**
 * Created by fantom on 02-Oct-17.
 */

public class GifFragment extends Fragment {
    private static final String KEY_TOKEN = "token";

    private CompositeDisposable disposables;

    static GifFragment newInstance(String token) {
        Bundle arguments = new Bundle();
        arguments.putString(KEY_TOKEN, token);
        GifFragment fragment = new GifFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    private String getToken(){
        return getArguments().getString(KEY_TOKEN);
    }

    @Inject ApiService apiService;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.ivGif) ImageView gifka;
    @OnClick(R.id.root_view) void rootClicked(){
        if(gifDrawable!=null){
            gifDrawable.stop();
            gifDrawable.recycle();
        }
        getActivity().onBackPressed();
    };

    private GifDrawable gifDrawable;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        DoItApp.getComponent(context).inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gif, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

    }

    @Override public void onResume() {
        super.onResume();
        disposables = new CompositeDisposable();
        if(isConnected()){
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setKeepScreenOn(true);
            disposables.add(apiService.getGif(getToken())
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(gifResponse -> {
                        if(gifResponse.isSuccessful()){
                            Timber.e("Link request: %s", gifResponse.body().getUrl());
                            Glide.with(getActivity())
                                    .asGif()
                                    .load(gifResponse.body().getUrl())
                                    .listener(new RequestListener<GifDrawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                                            progressBar.setVisibility(View.GONE);
                                            gifDrawable = resource;
                                            return false;
                                        }
                                    })
                                    .into(gifka);
                        } else {
                            showError(gifResponse.errorBody().string());
                        }
                    }, e -> showError(e.getMessage()))
            );
        }

    }

    @Override public void onPause() {
        super.onPause();
        disposables.dispose();
        progressBar.setKeepScreenOn(false);
    }

    private boolean isConnected() {
        if (!Connectivity.isConnected(getContext())) {
            Snackbar.make(progressBar, R.string.no_connection, Snackbar.LENGTH_LONG)
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

    private void showError(String errorText){
        Timber.e(errorText);
        Snackbar.make(progressBar,errorText,Snackbar.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }
}
