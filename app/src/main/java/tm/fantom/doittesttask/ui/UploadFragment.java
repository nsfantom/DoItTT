package tm.fantom.doittesttask.ui;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.media.ExifInterface;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import timber.log.Timber;
import tm.fantom.doittesttask.DoItApp;
import tm.fantom.doittesttask.R;
import tm.fantom.doittesttask.api.ApiService;
import tm.fantom.doittesttask.api.model.UploadImage;
import tm.fantom.doittesttask.util.Connectivity;
import tm.fantom.doittesttask.util.ExifParser;
import tm.fantom.doittesttask.util.PermissionChecker;

/**
 * Created by fantom on 02-Oct-17.
 */

public class UploadFragment extends Fragment {
    private static final String KEY_TOKEN = "token";
    private static final int SELECT_IMAGE_CODE = 101;

    private CompositeDisposable disposables;
    private UploadImage uploadImage;

    static UploadFragment newInstance(String token) {
        Bundle arguments = new Bundle();
        arguments.putString(KEY_TOKEN, token);
        UploadFragment fragment = new UploadFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    private String getToken(){
        return getArguments().getString(KEY_TOKEN);
    }

    @Inject ApiService apiService;
    @Inject PermissionChecker permissionChecker;
    @Inject ContentResolver contentResolver;
    @Inject FusedLocationProviderClient providerClient;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.ivImage) ImageView imageToUpload;
    @BindView(R.id.etDescription) EditText etDescription;
    @BindView(R.id.etHashTag) EditText etHashTag;


    @Override public void onAttach(Context context) {
        super.onAttach(context);
        DoItApp.getComponent(context).inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        uploadImage = new UploadImage();
    }

    @Override public void onResume() {
        super.onResume();
        disposables = new CompositeDisposable();
    }

    @Override public void onPause() {
        super.onPause();
        disposables.dispose();
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

    @OnClick(R.id.ivImage) void imageClick(){
        pickImage();
    }

    @OnClick(R.id.ivUpload) void uploadClicked(){
        uploadImage.setDescription(etDescription.getText().toString());
        uploadImage.setHashTag(etHashTag.getText().toString());

        if(!isConnected()) return;
        if (uploadImage.getImageFile() != null) {
            if (uploadImage.getLatitude() != 0 && uploadImage.getLongitude() != 0) {
                progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent),
                        android.graphics.PorterDuff.Mode.SRC_IN);
                progressBar.setVisibility(View.VISIBLE);

                RequestBody imageReq = RequestBody.create(MediaType.parse("image/*"), uploadImage.getImageFile());
                disposables.add(apiService.uploadImage(getToken(),
                        MultipartBody.Part.createFormData("image", uploadImage.getImageFile().getName(), imageReq),
                        stringToRequestBody(uploadImage.getDescription()),
                        stringToRequestBody(uploadImage.getHashTag()),
                        stringToRequestBody((String.valueOf(uploadImage.getLatitude()))),
                        stringToRequestBody(String.valueOf(uploadImage.getLongitude())))
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(responseBodyResponse -> {
                            if (responseBodyResponse.isSuccessful()) {
                                getActivity().onBackPressed();
                            } else {
                                showError(responseBodyResponse.errorBody().string()/*error*/);
                            }
                        })
                );
            } else {
                showError(getString(R.string.location_permission_needed));
            }
        } else {
            showError(getString(R.string.error_image_select));
        }
    }

    private RequestBody stringToRequestBody(String string) {
        return RequestBody.create(MediaType.parse("text/plain"), string);
    }

    public void pickImage() {
        if (permissionChecker.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startPickImageIntent();
        } else {
            permissionChecker.requestForWriteStoragePermission();
        }
    }

    private void startPickImageIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_IMAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                try {
                    String fileName = data.getData().getLastPathSegment();
                    uploadImage.setImageFile(File.createTempFile(fileName, null, getActivity().getCacheDir()));
                    Bitmap selectedImage = MediaStore.Images.Media.getBitmap(contentResolver, data.getData());
                    FileOutputStream fos = new FileOutputStream(uploadImage.getImageFile());
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 70, fos);
                    fos.close();

                    selectedImage.recycle(); selectedImage = null;

                    Glide.with(this)
                            .load(data.getData())
                            .into(imageToUpload);
                    ExifInterface exifInterface = new ExifInterface(contentResolver.openInputStream(data.getData()));

                    ExifParser exifParser = new ExifParser(exifInterface);

                    if (exifParser.isValid()) {
                        uploadImage.setLatitude(exifParser.getLatitude());
                        uploadImage.setLongitude(exifParser.getLongitude());

                        Timber.e("image location: %s", exifParser.toString());
                    } else {
                        getLastKnownLocation();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionChecker.WRITE_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPickImageIntent();
            } else {
                showError(getString(R.string.storage_permission_needed));
                //show user that we need this permission
            }
        } else if (requestCode == PermissionChecker.LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation();
            } else {
                showError(getString(R.string.location_permission_needed));
            }
        }
    }

    @SuppressWarnings({"MissingPermission"})
    public void getLastKnownLocation() {
        if (permissionChecker.isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            providerClient.getLastLocation().addOnSuccessListener(location -> {
                if (location!=null && location.getLatitude() != 0 && location.getLongitude() != 0) {
                    uploadImage.setLatitude((float) location.getLatitude());
                    uploadImage.setLongitude((float) location.getLongitude());
                } else {
                    showError(getString(R.string.error_getting_location));
                }
            });
        } else {
            permissionChecker.requestForLocationPermission();
        }
    }

    private void showError(String errorText){
        Timber.e(errorText);
        Snackbar.make(progressBar,errorText,Snackbar.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
    }

}
