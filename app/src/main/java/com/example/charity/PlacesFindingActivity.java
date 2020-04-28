package com.example.charity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesFindingActivity extends AppCompatActivity {

    private static final String TAG = PlacesFindingActivity.class.getSimpleName();
    private static final String SEARCH_QUERY = "search_query";

    // Get search view reference
    @BindView(R.id.search_view_for_benefactors)
    SearchView mSearchView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private PlacesClient mPlacesClient;
    //Google account Api key
    private static String API_KEY1 = "AIzaSyCeOX-6wnF-i2hiWcwFRUWuqt4Cgbib7KA";
    private static String API_KEY2 = "AIzaSyBDTy_VMG4yVBe36tWMftmO-kXmjpenODg";

    public static String API_KEY = API_KEY2;
    private int i = 1;
    public static final String PLACE_SEARCHED_BUNDLE = "places_budle";
    public static final String PLACES_IDS_EXTRA = "places_ids";;
    public static final String PLACES_NAMES_EXTRA = "places_names";
    public static final String PLACES_ADDRESSES_EXTRA = "places_addresses";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_finding);
        // Bind butterKnife library to activity
        ButterKnife.bind(this);
        // Initialize the SDK
        Places.initialize(getApplicationContext(), API_KEY);
        // Create a new Places client instance
        mPlacesClient = Places.createClient(this);
        handleIntent(getIntent());

        // Setup the search view
        SearchManager searchManager = (SearchManager) getSystemService(
                Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intentToCurrentActivity = new Intent(PlacesFindingActivity.this, PlacesFindingActivity.class);
                intentToCurrentActivity.setAction(Intent.ACTION_SEARCH);
                intentToCurrentActivity.putExtra(SEARCH_QUERY, query);
                startActivity(intentToCurrentActivity);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
        super.onNewIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String searchQuery;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SEARCH_QUERY);
            // Handle the search content
            Log.i(TAG, "The search query is: " + searchQuery);
            // Hide search View and display progress bar
            mSearchView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            // Launch places finding request
            this.findPlacesList(this, mPlacesClient, searchQuery);
        }
    }

    /**
     *  Finds benefactors places location from google server
     *
     * @param context       Activity context
     * @param placesClient  PlacesClient initialized in Activity
     * @param searchQuery   Query entered from user
     */

    public void findPlacesList(Context context, PlacesClient placesClient, String searchQuery) {

        // Create a new token for the autocomplete session.
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a dummy RectangularBounds object for testing purposes.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));
        // Create a FindAutocompletePredictionsRequest instance.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(bounds)
                .setOrigin(new LatLng(28.0339, 1.6596 ))
                .setSessionToken(token)
                .setQuery(searchQuery)
                .build();
        // Set findAutocompletePredictions
        placesClient.findAutocompletePredictions(request).addOnSuccessListener(
                findAutocompletePredictionsResponse -> {
                    ArrayList<String> placesIds = new ArrayList<>();;
                     ArrayList<String> placesNames = new ArrayList<>();
                     ArrayList<String> placesAddresses = new ArrayList<>();
                     List<AutocompletePrediction> mPlaces = new ArrayList<>();
                    mPlaces =
                            findAutocompletePredictionsResponse.getAutocompletePredictions();
                    //
                    if( mPlaces.size() > 0) {
                        Log.i(TAG, "Places found : " + mPlaces);
                        // Go to listing places activity
                        Intent intentToDetailsPlacesActivity = new Intent(PlacesFindingActivity.this, SearchResultsActivity.class );
                        for(int i = 0; i < mPlaces.size(); i++) {
                            placesIds.add(mPlaces.get(i).getPlaceId());
                            placesNames.add(mPlaces.get(i).getPrimaryText(null).toString());
                            placesAddresses.add(mPlaces.get(i).getSecondaryText(null).toString());
                        }
                        // Create bundle to store place name and addresses to send them with intent
                        Bundle bundlePlaceDetails = new Bundle();
                        bundlePlaceDetails.putStringArrayList(PLACES_IDS_EXTRA,placesIds);
                        bundlePlaceDetails.putStringArrayList(PLACES_NAMES_EXTRA,placesNames);
                        bundlePlaceDetails.putStringArrayList(PLACES_ADDRESSES_EXTRA,placesAddresses);
                        intentToDetailsPlacesActivity.putExtras(bundlePlaceDetails);
                        startActivity(intentToDetailsPlacesActivity);
                    } else {
                        Log.i(TAG, getString(R.string.place_not_found_str));
                        Toast.makeText(this, getString(R.string.place_not_found_str),Toast.LENGTH_SHORT).show();
                    }
                    // Display search view and hide progress
                    mProgressBar.setVisibility(View.GONE);
                    mSearchView.setVisibility(View.VISIBLE);
                }
        ).addOnFailureListener(exception -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, context.getString(R.string.place_not_found_str) + apiException.getStatusCode() + " " + apiException.getStatusMessage());
                Toast.makeText(context, context.getString(R.string.place_not_found_Api_error),Toast.LENGTH_SHORT).show();
                // Display search view and hide progress
                mProgressBar.setVisibility(View.GONE);
                mSearchView.setVisibility(View.VISIBLE);            }
        });
    }

}
