package com.example.charity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.charity.adapters.PlacesDetailsAdapter;
import com.example.charity.database.PlaceContract;
import com.example.charity.utils.Utils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment implements PlacesDetailsAdapter.OnClickRecyclerViewItem {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private static final String TAG = DetailsFragment.class.getSimpleName();
    private PlacesClient mPlacesClient;
    private int mIndex = 0;
    private List<Place> mPlacesList = new ArrayList<>();
    private int mCursorDataCount;
    private PlacesDetailsAdapter mAdapter;
    private NavController mNavController;
    private boolean needToUpdateLocals = true;
    // Get references views
    @BindView(R.id.recylerViewList)
    RecyclerView mRecyclerView;
    @BindView(R.id.ProgressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.register_locals_button)
    Button mRegisterLocalsButton;
    @BindView(R.id.no_places_tv)
    TextView mNoLocalsTv;

    //@BindView(R.id.search_locals_legend_tv) TextView searchLocalsLegendTv ;
    @BindView(R.id.goto_settings_buttons)
    Button gotoSettingsButtons;

    public static final String STRING_KEY_PLACE_NAME = "place_name_key";
    public static final String STRING_KEY_PLACE_ADDRESS = "place_address_key";
    public static final String STRING_KEY_PLACE_PHONE = "place_phone_key";
    public static final String STRING_KEY_PLACE_WEBSITE = "place_website_key";
    public static final String STRING_KEY_PLACE_LONGITUDE = "place_longitude_key";
    public static final String STRING_KEY_PLACE_LATITUDE = "place_longitude_key";
    private View mView;
    private Context mContext;

    public DetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailsFragment.
     */
    public static DetailsFragment newInstance(String param1, String param2) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind butterKnife library to activity
        ButterKnife.bind(this, view);
        mView = view;
        // Get navigation controller
        mNavController = Navigation.findNavController(view);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();

        /*TEMPO*//*TEMPO*/
        // Request only if we go back from settings activity
        if(needToUpdateLocals) {
            findLocals();
        }
        // To prevent request server after returning from home or recent buttons clicking
        needToUpdateLocals = false;
    }

    private void findLocals() {
        gotoSettingsButtons.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
        mRegisterLocalsButton.setVisibility(View.VISIBLE);
        mNoLocalsTv.setText(getString(R.string.no_places_in_data_provider_str));
        /**
         * Check if wifi or mobile data are enabled
         * Hide search view, write explanation in text view
         */
        if (!Utils.checkInternetAvailability(mContext)) {
            gotoSettingsButtons.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            mRegisterLocalsButton.setVisibility(View.GONE);
            mNoLocalsTv.setText(getString(R.string.wifi_not_enabled));
            // Attach click listener for enable Internet Button
            gotoSettingsButtons.setOnClickListener(view1 -> {
                Intent intentToInternetSettings = new Intent(Settings.ACTION_WIFI_SETTINGS);
                if (intentToInternetSettings.resolveActivity(mContext.getPackageManager()) != null) {
                    startActivity(intentToInternetSettings);
                    needToUpdateLocals = true;
                }
            });
            return;
        }

        // Fetch locals if internet and GPS are enabled
        fetchLocals();
    }

    // mSearchView.setVisibility(View.VISIBLE);
    //gotoSettingsButtons.setVisibility(View.GONE);
    // mNoLocalsTv.setText(getString(R.string.search_benefactor_legend_text));

    // Launch place searching request
    //searchPlaces();

    private void fetchLocals() {
        /*
         * Attach click listener to register locals button
         * When clicked go to
         */
        mRegisterLocalsButton.setOnClickListener(view1 ->
                mNavController.navigate(R.id.action_detailsFragment_to_searchFragment));

        // set up the recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new PlacesDetailsAdapter(mContext, null, this);
        mRecyclerView.setAdapter(mAdapter);
        /**
         * When return back from next fragment(Contact fragment)
         * places List not destroyed, so we populate the recycler from
         * Existing places List object, don't need to request them from server
         */
        if (mPlacesList != null && mPlacesList.size() > 0) {
            mAdapter.swapPlaces(mPlacesList);
            //mRecyclerView.setAdapter(mAdapter);
            //mRecyclerView.setAdapter(mAdapter);
            mProgressBar.setVisibility(View.GONE);
            mRegisterLocalsButton.setVisibility(View.GONE);
            mNoLocalsTv.setVisibility(View.GONE);
            return;
        }

        // Initialize the SDK
        Places.initialize(mContext, SearchFragment.API_KEY);
        // Create a new Places client instance
        mPlacesClient = Places.createClient(mContext);
        // Query places Ids from database
        Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
        final Cursor data = getActivity().getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);
        if (data == null || data.getCount() == 0) {
            /*Toast.makeText(mContext,
                    getString(R.string.no_places_in_data_provider_str),
                    Toast.LENGTH_LONG).show();*/
            mProgressBar.setVisibility(View.GONE);
            return;
        }
        /*
         *  Hide register local button & no places textview
         * if locals already registered in provider
         */
        mRegisterLocalsButton.setVisibility(View.GONE);
        mNoLocalsTv.setVisibility(View.GONE);
        mCursorDataCount = data.getCount();

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (data.moveToNext()) {
                    try {
                        Thread.sleep(60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    /*Find places*/
                    /*Find places*/
                    String placeId = data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACE_ID));
                    findPlacesListByPlacesIds(placeId);
                    Log.e("Thread", "while loop");
                }
                data.close();
            }
        };
        //Start new thread
        thread.start();
    }

    /**
     * Finds benefactors places location from google server
     *
     * @param placeIdsQuery Place id read from provider Query entered from user
     */

    public void findPlacesListByPlacesIds(String placeIdsQuery) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI,
                Place.Field.TYPES,
                Place.Field.LAT_LNG);
        // Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeIdsQuery, placeFields).build();
        Task<FetchPlaceResponse> fetchPlaceResponseTask = mPlacesClient.fetchPlace(request);
        // Add a listener to handle the response.
        fetchPlaceResponseTask.addOnSuccessListener(fetchPlaceResponse -> {
            Place place = fetchPlaceResponse.getPlace();
            if (!TextUtils.isEmpty(place.getPhoneNumber())) {
                mPlacesList.add(place);
            }
            if (mIndex == mCursorDataCount) {
                mAdapter.swapPlaces(mPlacesList);
                mProgressBar.setVisibility(View.GONE);
                Log.i(TAG, "Places found: " + place.getName());
            }
        });

        fetchPlaceResponseTask.addOnFailureListener(exception -> {
            if (exception instanceof ApiException) {
                if (mPlacesList.size() > 0 && mIndex == mCursorDataCount) {
                    mAdapter.swapPlaces(mPlacesList);
                    mProgressBar.setVisibility(View.GONE);
                }
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                Log.e(TAG, "Place not found: " + exception.getMessage() + ", statusCode = " + statusCode);
            }

        });
        mIndex++;
    }

    @Override
    public void onItemClick(int positionAdapter) {
        Place place = mPlacesList.get(positionAdapter);
        // Fill extras bundle of Intent
        Bundle bundleToDetails = new Bundle();
        bundleToDetails.putString(STRING_KEY_PLACE_NAME, getString(R.string.data_not_available_str));
        if (!TextUtils.isEmpty(place.getName())) {
            bundleToDetails.putString(STRING_KEY_PLACE_NAME, place.getName());
        }
        bundleToDetails.putString(STRING_KEY_PLACE_ADDRESS, getString(R.string.data_not_available_str));
        if (!TextUtils.isEmpty(place.getAddress())) {
            bundleToDetails.putString(STRING_KEY_PLACE_ADDRESS, place.getAddress());
        }
        bundleToDetails.putString(STRING_KEY_PLACE_PHONE, getString(R.string.data_not_available_str));
        if (!TextUtils.isEmpty(place.getPhoneNumber())) {
            bundleToDetails.putString(STRING_KEY_PLACE_PHONE, place.getPhoneNumber());
        }
        bundleToDetails.putString(STRING_KEY_PLACE_WEBSITE, getString(R.string.data_not_available_str));
        if (place.getWebsiteUri() != null) {
            bundleToDetails.putString(STRING_KEY_PLACE_WEBSITE, place.getWebsiteUri().toString());
        }
        if (place.getLatLng() != null) {
            bundleToDetails.putDouble(STRING_KEY_PLACE_LONGITUDE, place.getLatLng().latitude);
        }
        if (place.getLatLng() != null) {
            bundleToDetails.putDouble(STRING_KEY_PLACE_LATITUDE, place.getLatLng().latitude);
        }
        // Navigate to details fragment
        NavController navController = Navigation.findNavController(mView);
        navController.navigate(R.id.action_detailsFragment_to_contactFragment, bundleToDetails);
        needToUpdateLocals = true;
    }
}
