package com.example.charity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 302;
    // Reference Navigation View
    @BindView(R.id.bottom_nav) BottomNavigationView mBottomNavigationView ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Request location fine permission
        requestPermission();
        // Bind view
        ButterKnife.bind(this);
        // Setup Bottom navigation View
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        // Inflate navigation menu
        int nm = mBottomNavigationView.getMaxItemCount();
       // mBottomNavigationView.inflateMenu(R.menu.menu_bottom_nav);
        NavigationUI.setupWithNavController(mBottomNavigationView,navController);
    }

    /**
     * Request fine location permission
     */
    public void requestPermission() {
        // Check if permission has not already granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check if the request dialog has already shown
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.w(TAG, getString(R.string.permission_rejected_str));
            } else {
                Log.e(TAG, getString(R.string.permission_requesting_str));
                //  request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            // Permission has been already granted
            Log.i(TAG, getString(R.string.permission_already_granted_str));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    Log.i(TAG, getString(R.string.permission_granted));
                } else {
                    // permission denied
                    Toast.makeText(this, R.string.permission_denied_message_str,Toast.LENGTH_LONG).show();
                    finish();
                    Log.e(TAG, getString(R.string.permission_denied_str));
                }
                return;
            }
        }
    }
}
