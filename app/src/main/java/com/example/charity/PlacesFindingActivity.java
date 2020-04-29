package com.example.charity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesFindingActivity extends AppCompatActivity {

    private static final String TAG = PlacesFindingActivity.class.getSimpleName();
    private static final String SEARCH_QUERY = "search_query";
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 302;
    private static final int SEARCH_SURFACE = 40 ; // 400 kilometers square / 20 kilo
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
    public static final String PLACE_SEARCHED_BUNDLE = "places_bundle";
    public static final String PLACES_IDS_EXTRA = "places_ids";;
    public static final String PLACES_NAMES_EXTRA = "places_names";
    public static final String PLACES_ADDRESSES_EXTRA = "places_addresses";
    private LatLng mCurrentLatLng;
    private String LATITUDE_EXTRA = "latitude_extra_value";
    private String LONGITUDE_EXTRA = "longitude_extra_value";
    private double mCurrentLongitude;
    private double mCurrentLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_finding);
        // Request location fine permission
        requestPermission();

        // Bind butterKnife library to activity
        ButterKnife.bind(this);
        // Initialize the SDK
        Places.initialize(getApplicationContext(), API_KEY);
        // Create a new Places client instance
        mPlacesClient = Places.createClient(this);
        List<Place.Field> placeFields = new ArrayList<>();
        placeFields.add(Place.Field.LAT_LNG);
        placeFields.add(Place.Field.NAME);
        placeFields.add(Place.Field.ADDRESS);
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();
        Task<FindCurrentPlaceResponse> place = mPlacesClient.findCurrentPlace(request);
        place.addOnSuccessListener(findCurrentPlaceResponse -> {
            List<PlaceLikelihood> placesLikelyHood = findCurrentPlaceResponse.getPlaceLikelihoods();
            Place placeFound = placesLikelyHood.get(0).getPlace();
            String address = placeFound.getAddress();
            String name = placeFound.getName();
            mCurrentLatLng = placeFound.getLatLng();
            Log.e(TAG, "Current place name: " + name);
            Log.e(TAG, "Current place address: " + address);
            Log.e(TAG, "Current place LatLng: " + placeFound.getLatLng());
        })
        .addOnFailureListener(e ->
                Log.e(TAG, "Failed to fetch current place: " + e.getLocalizedMessage()));
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
                // put Latitude & longitude in intent
                if (mCurrentLatLng != null) {
                    Bundle bundle = new Bundle();
                    bundle.putDouble(LATITUDE_EXTRA,mCurrentLatLng.latitude);
                    bundle.putDouble(LONGITUDE_EXTRA,mCurrentLatLng.longitude);
                    intentToCurrentActivity.putExtras(bundle);
                }
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
            // Get longitude & latitude after clicking search view button intent
            // because it will be null after submitting
            mCurrentLatitude = intent.getExtras().getDouble(LATITUDE_EXTRA);
            mCurrentLongitude = intent.getExtras().getDouble(LONGITUDE_EXTRA);
            mCurrentLatLng = new LatLng(mCurrentLatitude, mCurrentLongitude);
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
        // Create a  RectangularBounds object
        RectangularBounds bounds = buildRectangularBoundsFromPosition(mCurrentLatLng, SEARCH_SURFACE);
        // Create a FindAutocompletePredictionsRequest instance.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(bounds)
                .setSessionToken(token)
                .setQuery(searchQuery)
                .build();
        // Set findAutocompletePredictions
        placesClient.findAutocompletePredictions(request).addOnSuccessListener(
                findAutocompletePredictionsResponse -> {
                    ArrayList<String> placesIds = new ArrayList<>();;
                     ArrayList<String> placesNames = new ArrayList<>();
                     ArrayList<String> placesAddresses = new ArrayList<>();
                     List<AutocompletePrediction> mPlaces =
                            findAutocompletePredictionsResponse.getAutocompletePredictions();
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
                Log.e(TAG, context.getString(R.string.place_not_found_str) + apiException.getStatusCode() + " " + apiException.getMessage());
                Toast.makeText(context, context.getString(R.string.place_not_found_Api_error),Toast.LENGTH_SHORT).show();
                // Display search view and hide progress
                mProgressBar.setVisibility(View.GONE);
                mSearchView.setVisibility(View.VISIBLE);            }
        });
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

    /**
     * Builds a rectangular bounds  from a given current latitude, longitude, surface to recover
     *
     * @param currentLatLng Current user position latitude, longitude
     * @param zoneRectangleSurface Surface in Kilometers² of the surface to recover in searching
     * @return RectangularBounds instance
     */
    public RectangularBounds buildRectangularBoundsFromPosition(LatLng currentLatLng, double zoneRectangleSurface) {
        // Algeria LatLng(36.55, 3.3)
        double distancePerDegreeLatitude = 111.699;
        double distancePerDegreeLongitude = 111.321;
        double lineLength = Math.sqrt(zoneRectangleSurface);
        double degreeShiftLatitude = (lineLength / 2) / distancePerDegreeLatitude;
        double degreeShiftLongitude = (lineLength / 2) / distancePerDegreeLongitude;

        double southwestLatitude = currentLatLng.latitude - degreeShiftLatitude;
        double southwestLongitude = currentLatLng.longitude - degreeShiftLongitude;

        double northeastLatitude = currentLatLng.latitude + degreeShiftLatitude;
        double northeastLongitude = currentLatLng.longitude + degreeShiftLongitude;

        return RectangularBounds.newInstance(new LatLng(southwestLatitude, southwestLongitude),
                new LatLng(northeastLatitude, northeastLongitude)
        );
    }
}
