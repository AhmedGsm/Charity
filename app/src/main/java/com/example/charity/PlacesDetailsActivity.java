package com.example.charity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class PlacesDetailsActivity extends AppCompatActivity {
    private static final String TAG = PlacesDetailsActivity.class.getSimpleName();
    private PlacesClient mPlacesClient;
    private int mIndex = 0;
    private List<Place> mPlacesList = new ArrayList<>();
    private int mCursorDataCount;
    private PlacesDetailsAdapter mAdapter;
    @BindView(R.id.recylerViewList) RecyclerView mRecyclerView;
    // Get search view reference
    @BindView(R.id.ProgressBar) ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_details);

        // Bind butterKnife library to activity
        ButterKnife.bind(this);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), PlacesFindingActivity.API_KEY);

        // Create a new Places client instance
        mPlacesClient = Places.createClient(this);

        // set up the recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PlacesDetailsAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        // Query places Ids from database
        Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
        final Cursor data = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);
        if (data == null || data.getCount() == 0) {
            Toast.makeText(this,R.string.no_places_in_data_provider_str,Toast.LENGTH_LONG).show();
            mProgressBar.setVisibility(View.GONE);
            return;
        }
        mCursorDataCount = data.getCount();
        while (data.moveToNext()) {
            String placeId = data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACE_ID));
            findPlacesListByPlacesIds(placeId);
        }
        data.close();
    }

    /**
     * Finds benefactors places location from google server
     *
     * @param placeIdsQuery Place id read from provider Query entered from user
     */

    public void findPlacesListByPlacesIds(String placeIdsQuery) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
                Place.Field.NAME,
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
            mPlacesList.add(place);
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
}
