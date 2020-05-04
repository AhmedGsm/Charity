package com.example.charity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private static final String TAG = MainActivity.class.getSimpleName();
    private LatLng mCurrentLatLng;
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
    public static final String PLACES_IDS_EXTRA = "places_ids";;
    public static final String PLACES_NAMES_EXTRA = "places_names";
    public static final String PLACES_ADDRESSES_EXTRA = "places_addresses";
    private  View mView;
    private NavController mNavController;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // initialize view Object with inflated fragment view
        mView = view;
        // Bind butterKnife library to activity
        ButterKnife.bind(this,view);
        // Launch place serching request
        searchPlaces();
        };


    public void searchPlaces() {
        // Initialize the SDK
        Places.initialize(getContext(), API_KEY);
        // Create a new Places client instance
        mPlacesClient = Places.createClient(getContext());
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

        // Setup the search view
        SearchManager searchManager = (SearchManager) getContext().getSystemService(
                Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void handleSearch(String searchQuery) {
            // Handle the search content
            Log.i(TAG, "The search query is: " + searchQuery);
            // Hide search View and display progress bar
            mSearchView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            // Launch places search request
            this.findPlacesList(getContext(), mPlacesClient, searchQuery);
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
                        for(int i = 0; i < mPlaces.size(); i++) {
                            placesIds.add(mPlaces.get(i).getPlaceId());
                            placesNames.add(mPlaces.get(i).getPrimaryText(null).toString());
                            placesAddresses.add(mPlaces.get(i).getSecondaryText(null).toString());
                        }
                        // Create bundle to store place name and addresses to send them to detail fragment
                        Bundle bundlePlaceDetails = new Bundle();
                        bundlePlaceDetails.putStringArrayList(PLACES_IDS_EXTRA,placesIds);
                        bundlePlaceDetails.putStringArrayList(PLACES_NAMES_EXTRA,placesNames);
                        bundlePlaceDetails.putStringArrayList(PLACES_ADDRESSES_EXTRA,placesAddresses);
                        // Navigate to search results Fragment
                        mNavController = Navigation.findNavController(mView);
                        mNavController.navigate(R.id.actionResults,bundlePlaceDetails);
                    } else {
                        Log.i(TAG, getString(R.string.place_not_found_str));
                        Toast.makeText(getContext(), getString(R.string.place_not_found_str),Toast.LENGTH_SHORT).show();
                    }
                    // Display search view and hide progress
                    mProgressBar.setVisibility(View.GONE);
                    mSearchView.setVisibility(View.VISIBLE);
                }
        ).addOnFailureListener(exception -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, context.getString(R.string.place_not_found_str) + " " + apiException.getStatusCode() + " " + apiException.getMessage());
                Toast.makeText(context, context.getString(R.string.place_not_found_Api_error,apiException.getMessage()),Toast.LENGTH_SHORT).show();
                // Display search view and hide progress
                mProgressBar.setVisibility(View.GONE);
                mSearchView.setVisibility(View.VISIBLE);
            }
        });
    }

}
