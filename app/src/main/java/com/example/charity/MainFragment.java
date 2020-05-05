package com.example.charity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Reference Views
    // Get search benefactors button reference
    @BindView(R.id.search_locals_btn) Button searchLocalBtn;
    @BindView(R.id.charity_introduction_tv) TextView introductionTextView;


    // Fields
    private boolean isPermissionGranted = false;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 302;
    private Context mContext;
    private View mView;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         // Get fragment view
        mView = view;
        // Bind butterKnife library to fragment
        ButterKnife.bind(this, view);
        // Request location fine permission
        requestPermission();
        // Set click listener for button to go to search activity
        searchLocalBtn.setOnClickListener(viewL -> {
            //searchPlaces();
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_mainFragment_to_detailsFragment);
        });

        /*
         *Check if the permission has already denied by user
         * Disable navigation view and button
         */
        searchLocalBtn.setVisibility(View.GONE);
        introductionTextView.setText(R.string.permission_not_granted);
        //
        if (isPermissionGranted) {
            // Enable views if permission granted
            searchLocalBtn.setVisibility(View.VISIBLE);
            introductionTextView.setText(R.string.charity_introduction);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Request fine location permission
     */
    public void requestPermission() {
        // Check if permission has not already granted
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check if the request dialog has already shown
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                isPermissionGranted = false;
                Log.w(TAG, getString(R.string.permission_already_rejected_str));
            } else {
                Log.e(TAG, getString(R.string.permission_requesting_str));
                //  request the permission
                requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            // Permission has been already granted
            isPermissionGranted = true;
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
                    isPermissionGranted = true;
                    // Enable views if permission granted
                    searchLocalBtn.setVisibility(View.VISIBLE);
                    introductionTextView.setText(R.string.charity_introduction);
                } else {
                    // permission denied
                    Snackbar.make(mView,R.string.permission_denied_message_str,Snackbar.LENGTH_LONG).show();
                    //finish();
                    Log.e(TAG, getString(R.string.permission_denied_str));
                    isPermissionGranted = false;
                }
                return;
            }
        }
    }
}
