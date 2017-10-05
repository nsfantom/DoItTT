package tm.fantom.doittesttask.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public final class PermissionChecker {

    public static final int WRITE_STORAGE_REQUEST_CODE = 1;
    public static final int LOCATION_REQUEST_CODE = 2;

    private List<String> permissionList = new ArrayList<>();

    private Context mContext;

    public PermissionChecker(Context context) {
        mContext = context;
    }

    public void requestForWriteStoragePermission() {
        requestForPermission(WRITE_STORAGE_REQUEST_CODE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void requestForLocationPermission() {
        requestForPermission(LOCATION_REQUEST_CODE, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public boolean isPermissionGranted(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(mContext, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestForPermission(int requestCode, @NonNull String... permissions) {
        for (String permission : permissions) {

            int hasPermission = mContext.checkSelfPermission(permission);

            if (hasPermission != PackageManager.PERMISSION_GRANTED)
                permissionList.add(permission);
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) mContext,
                    permissionList.toArray(new String[permissionList.size()]),
                    requestCode);
        }

    }
}