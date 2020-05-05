package com.example.charity;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    // Reference Views
    @BindView(R.id.bottom_nav) BottomNavigationView mBottomNavigationView ;

    // Fields
    private boolean isPermissionGranted = false;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 302;

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();}

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind butterKnife library to activity
        ButterKnife.bind(this);
        /*
         *Check if the permission has already denied by user
         * Disable navigation view and button
         */
        //*******//mBottomNavigationView.setVisibility(View.GONE);
        //mBottomNavigationView.setEnabled(false);
        //mBottomNavigationView.
        //
        if (isPermissionGranted) {
            // Enable views if permission granted
            mBottomNavigationView.setVisibility(View.VISIBLE);
        }
        // Setup Bottom navigation View
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(mBottomNavigationView,navController);
    }
}
