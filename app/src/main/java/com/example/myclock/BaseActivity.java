package com.example.myclock;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = getClass().getSimpleName();
    private boolean showSysUI = true;

    private static final int REQUEST_PERMISSION = 1;
    private final String[] PERMISSIONS = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.CAMERA,
//            Manifest.permission.READ_PHONE_STATE,
//            Manifest.permission.INTERNET
    };

    protected enum ScreenOrientation {
        Portrait,
        Landscape
    }

    public interface OnPermissionListener {
        void onResult(boolean success);
    }

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    protected void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    protected void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    protected boolean checkPermission(String permission) {

        int state = ContextCompat.checkSelfPermission(this, permission);
        if (state != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
//            case android.R.id.undo:
//                onBackPressed();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected boolean checkPermissions() {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            int state = ContextCompat.checkSelfPermission(this, PERMISSIONS[i]);
            if (state != PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] != PERMISSION_GRANTED) {
                Log.e(TAG, "onRequestPermissionsResult f");
                Toast.makeText(this, "请打开权限", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION);
            } else {
                Log.e(TAG, "onRequestPermissionsResult e");
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    protected void setOrientation(ScreenOrientation orientation) {
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                && orientation == ScreenOrientation.Landscape){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                && orientation == ScreenOrientation.Portrait) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}
