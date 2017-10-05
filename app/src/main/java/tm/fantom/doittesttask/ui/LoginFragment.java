package tm.fantom.doittesttask.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;
import tm.fantom.doittesttask.DoItApp;
import tm.fantom.doittesttask.R;
import tm.fantom.doittesttask.api.ApiService;
import tm.fantom.doittesttask.api.model.User;
import tm.fantom.doittesttask.util.ImageOps;
import tm.fantom.doittesttask.util.PermissionChecker;
import tm.fantom.doittesttask.util.SessionStorage;

import static android.util.Patterns.EMAIL_ADDRESS;

/**
 * Created by fantom on 29-Sep-17.
 */

public class LoginFragment extends Fragment {
    private static final int SELECT_IMAGE_CODE = 101;
    private CompositeDisposable disposables;
    private User user;

    interface Listener{
        void onLogin(String email, String password);
        void onRegistration(User user);
    }

    static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Inject ApiService apiService;
    @Inject SessionStorage sessionStorage;
    @Inject PermissionChecker permissionChecker;
    @BindView(R.id.login_form) ViewGroup vgLogin;
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView(R.id.etPassword) EditText etPassword;

    @BindView(R.id.register_form) ViewGroup vgRegister;
    @BindView(R.id.ivAvatar) ImageView ivAvatar;
    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etRegEmail) EditText etRegEmail;
    @BindView(R.id.etRegPassword) EditText etRegPassword;
    @BindView(R.id.etReRegPassword) EditText etRepass;


    private Listener listener;

    @Override public void onAttach(Context context) {
        if (!(getActivity() instanceof Listener)) {
            throw new IllegalStateException("Activity must implement fragment Listener.");
        }
        super.onAttach(context);
        DoItApp.getComponent(context).inject(this);
        listener = (Listener) getActivity();
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                       @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        user = new User();
    }


    @Override public void onResume() {
        super.onResume();
        disposables = new CompositeDisposable();

    }

    @Override public void onPause() {
        super.onPause();
        disposables.dispose();
    }

    private void toggleViewGroup() {
        if (vgLogin.getVisibility() == View.VISIBLE) {
            vgLogin.setVisibility(View.GONE);
            vgRegister.setVisibility(View.VISIBLE);
        } else {
            vgRegister.setVisibility(View.GONE);
            vgLogin.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.ivAvatar) void avatarClick(){
        pickImage();
    }

    @OnClick(R.id.tvToLogin) void toLogin(){
        toggleViewGroup();
    }
    @OnClick(R.id.tvToRegistration) void toRegistration(){
        toggleViewGroup();
    }
    @OnClick(R.id.sign_up_button) void tryReg(){
        // Reset errors.
        etRegEmail.setError(null);
        etRegPassword.setError(null);
        etRepass.setError(null);

        // Store values at the time of the update attempt.
        String email = etRegEmail.getText().toString();
        String newPassword = etRegPassword.getText().toString();
        String repeatNewPassword = etRepass.getText().toString();

        boolean cancel = false;
        View focusView = null;
        // Check for a valid old password, if the user entered one.
        if (!TextUtils.isEmpty(email) && !EMAIL_ADDRESS.matcher(email).matches()) {
            focusView = etRegEmail;
            etRegEmail.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }

        if (!cancel && !isPasswordValid(newPassword)) {
            // Check for a valid new password, if the user entered one.
            focusView = etRegPassword;
            etRegPassword.setError(getString(R.string.error_incorrect_password));
            cancel = true;
        }

        if (!cancel && !newPassword.equals(repeatNewPassword)) {
            focusView = etRepass;
            etRepass.setError(getString(R.string.error_not_match_password));
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            user.setUsername(etUsername.getText().toString());
            user.setEmail(email);
            user.setPassword(newPassword);
            if(user.getAvatar()==null) {
                saveImage(((BitmapDrawable)ivAvatar.getDrawable()).getBitmap());
            }
            listener.onRegistration(user);
        }
    }
    @OnClick(R.id.sign_in_button) void tryLogin(){
        // Reset errors.
        etEmail.setError(null);
        etPassword.setError(null);

        // Store values at the time of the update attempt.
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;
        // Check for a valid old password, if the user entered one.
        if (!TextUtils.isEmpty(email) && !EMAIL_ADDRESS.matcher(email).matches()) {
            focusView = etEmail;
            etEmail.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }

        if (!cancel && !isPasswordValid(password)) {
            // Check for a valid new password, if the user entered one.
            focusView = etPassword;
            etPassword.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            listener.onLogin(email,password);
        }
    }

    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 3;
    }

    public void pickImage() {
        if (permissionChecker.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startPickImageIntent();
        } else {
            permissionChecker.requestForWriteStoragePermission();
        }
    }

    private void startPickImageIntent() {
        Intent i = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECT_IMAGE_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap selectedBitmap = null;
                try {
                    selectedBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                } catch (IOException e) {
                    Timber.e(e.getMessage(), e);
                }

                Bitmap newBitmap = ImageOps.scaleDown(selectedBitmap, 500, true);
                selectedBitmap.recycle();
                selectedBitmap = null;

                saveImage(newBitmap);
            }
        }
    }

    private void saveImage(Bitmap newBitmap) {
        File newImageFile = ImageOps.createFile(ImageOps.getImageDir(getContext()), SessionStorage.KEY_AVATAR_FILE_NAME);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(newImageFile);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            fos.close();
        } catch (Exception e) {
            Timber.e(e.getMessage(), e);
        }
        Timber.e("image file: %s", newImageFile.getAbsolutePath());
        user.setAvatar(newImageFile);
        Glide.with(this)
                .load(newImageFile)
                .into(ivAvatar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            startPickImageIntent();
        }
    }

}
