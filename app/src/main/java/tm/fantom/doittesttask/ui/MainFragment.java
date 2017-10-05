package tm.fantom.doittesttask.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import tm.fantom.doittesttask.adapter.ImageAdapter;
import tm.fantom.doittesttask.api.ApiService;
import tm.fantom.doittesttask.util.Connectivity;
import tm.fantom.doittesttask.util.SessionStorage;

/**
 * Created by fantom on 02-Oct-17.
 */

public class MainFragment extends Fragment {

    private CompositeDisposable disposables;
    private ImageAdapter adapter;

    interface Listener{
        void onUploadClicked(String token);
    }

    static MainFragment newInstance() {
        return new MainFragment();
    }

    @Inject ApiService apiService;
    @Inject SessionStorage sessionStorage;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.rvImages) RecyclerView rvImages;

    private Listener listener;
    private GifDrawable gifDrawable;


    @Override public void onAttach(Context context) {
        if (!(getActivity() instanceof Listener)) {
            throw new IllegalStateException("Activity must implement fragment Listener.");
        }
        super.onAttach(context);
        DoItApp.getComponent(context).inject(this);
        adapter = new ImageAdapter();
        listener = (Listener) getActivity();
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        rvImages.setLayoutManager(new GridLayoutManager(getContext(),2));
        rvImages.setAdapter(adapter);
    }

    @Override public void onResume() {
        super.onResume();
        disposables = new CompositeDisposable();
        if(isConnected()){
            progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setVisibility(View.VISIBLE);
            disposables.add(apiService.getAllImages(sessionStorage.getToken())
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(imageListResponse -> {
                        if(imageListResponse.isSuccessful() && imageListResponse.body().getImageList()!=null){
                            adapter.accept(imageListResponse.body().getImageList());
                            progressBar.setVisibility(View.GONE);
                        } else {
                            showError(imageListResponse.errorBody().string());
                        }
                    })
            );
        }
    }

    @Override public void onPause() {
        super.onPause();
        disposables.dispose();
    }

    @OnClick(R.id.ivAdd) void onAdd(){
        listener.onUploadClicked(sessionStorage.getToken());
    }

    @OnClick(R.id.ivPlay) void onPlay(){
        Dialog d = getDialog();
        d.setContentView(getActivity().getLayoutInflater().inflate(R.layout.fragment_gif, null));
        d.setOnDismissListener(dialogInterface -> {
            if(gifDrawable!=null){
                gifDrawable.stop();
                gifDrawable.recycle();
            }
        });
        ProgressBar dialogProgressBar = d.findViewById(R.id.progressBar);
        ImageView gifka = d.findViewById(R.id.ivGif);
        if(isConnected()){
            dialogProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent),
                    android.graphics.PorterDuff.Mode.SRC_IN);
            dialogProgressBar.setVisibility(View.VISIBLE);
            disposables.add(apiService.getGif(sessionStorage.getToken())
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
                                            dialogProgressBar.setVisibility(View.GONE);
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
            d.show();
        }
    }

    private Dialog getDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void showError(String errorText){
        Timber.e(errorText);
        Snackbar.make(progressBar,errorText,Snackbar.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
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
}
