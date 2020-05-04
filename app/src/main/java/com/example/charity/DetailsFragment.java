package com.example.charity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.charity.adapters.PlacesDetailsAdapter;
import com.example.charity.database.PlaceContract;
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
public class DetailsFragment extends Fragment implements PlacesDetailsAdapter.OnClickRecyclerViewItem{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String TAG = PlacesDetailsActivity.class.getSimpleName();
    private PlacesClient mPlacesClient;
    private int mIndex = 0;
    private List<Place> mPlacesList = new ArrayList<>();
    private int mCursorDataCount;
    private PlacesDetailsAdapter mAdapter;
    @BindView(R.id.recylerViewList)
    RecyclerView mRecyclerView;
    // Get search view reference
    @BindView(R.id.ProgressBar)
    ProgressBar mProgressBar;
    public static final String STRING_KEY_PLACE_NAME = "place_name_key";
    public static final String STRING_KEY_PLACE_ADDRESS = "place_address_key";
    public static final String STRING_KEY_PLACE_PHONE = "place_phone_key";
    public static final String STRING_KEY_PLACE_WEBSITE = "place_website_key";
    public static final String STRING_KEY_PLACE_LONGITUDE = "place_longitude_key";
    public static final String STRING_KEY_PLACE_LATITUDE = "place_longitude_key";

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
    // TODO: Rename and change types and number of parameters
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
        ButterKnife.bind(this,view);

        // Initialize the SDK
        Places.initialize(getContext(), SearchFragment.API_KEY);

        // Create a new Places client instance
        mPlacesClient = Places.createClient(getContext());

        // set up the recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PlacesDetailsAdapter(getContext(), null, this);
        mRecyclerView.setAdapter(mAdapter);
        // Query places Ids from database
        Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
        final Cursor data = getActivity().getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);
        if (data == null || data.getCount() == 0) {
            Toast.makeText(getContext(), R.string.no_places_in_data_provider_str, Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.GONE);
            return;
        }
        mCursorDataCount = data.getCount();

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (data.moveToNext()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
        Bundle bundle = new Bundle();
        bundle.putString(STRING_KEY_PLACE_NAME, getString(R.string.data_not_available_str));
        if (!TextUtils.isEmpty(place.getName())) {
            bundle.putString(STRING_KEY_PLACE_NAME, place.getName());
        }
        bundle.putString(STRING_KEY_PLACE_ADDRESS, getString(R.string.data_not_available_str));
        if (!TextUtils.isEmpty(place.getAddress())) {
            bundle.putString(STRING_KEY_PLACE_ADDRESS, place.getAddress());
        }
        bundle.putString(STRING_KEY_PLACE_PHONE, getString(R.string.data_not_available_str));
        if (!TextUtils.isEmpty(place.getPhoneNumber())) {
            bundle.putString(STRING_KEY_PLACE_PHONE, place.getPhoneNumber());
        }
        bundle.putString(STRING_KEY_PLACE_WEBSITE, getString(R.string.data_not_available_str));
        if (place.getWebsiteUri() != null) {
            bundle.putString(STRING_KEY_PLACE_WEBSITE, place.getWebsiteUri().toString());
        }
        if (place.getLatLng() != null) {
            bundle.putDouble(STRING_KEY_PLACE_LONGITUDE, place.getLatLng().latitude);
        }
        if (place.getLatLng() != null) {
            bundle.putDouble(STRING_KEY_PLACE_LATITUDE, place.getLatLng().latitude);
        }
        // Initialize intent to contact activity
        Intent intentToContactActivity = new Intent(getContext(), ContactActivity.class);
        intentToContactActivity.putExtras(bundle);
        // Launch contact activity
        startActivity(intentToContactActivity);
    }

}
