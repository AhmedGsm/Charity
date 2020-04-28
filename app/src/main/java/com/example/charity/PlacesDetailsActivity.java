package com.example.charity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.charity.database.PlaceContract;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PlacesDetailsActivity extends AppCompatActivity {
    private static final String TAG = PlacesDetailsActivity.class.getSimpleName();
    private PlacesClient mPlacesClient;
    private int mIndex = 0;
    private List<Place> mPlaceList = new ArrayList<>();
    private int mCursorDataCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_details);
        //

        // Initialize the SDK
        Places.initialize(getApplicationContext(), PlacesFindingActivity.API_KEY);

        // Create a new Places client instance
        mPlacesClient = Places.createClient(this);

        // Query places Ids from database
        Uri uri = PlaceContract.PlaceEntry.CONTENT_URI;
        final Cursor data = getContentResolver().query(
                uri,
                null,
                null,
                null,
                null);
        mCursorDataCount = data.getCount();
        if (data == null || mCursorDataCount == 0) return;
        while (data.moveToNext()) {
            String placeId = data.getString(data.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACE_ID));
            findPlacesListByPlacesIds(placeId);
        }
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
            mPlaceList.add(place);
            if (mIndex == mCursorDataCount) {
                //mAdapter.swapPlaces(mPlaceList);
                //mGeofencing.updateGeofencesList(mPlaceList);
                //if (mIsEnabled) mGeofencing.registerAllGeofences();
                Log.i(TAG, "Place found: " + place.getName());
            }
        });

        fetchPlaceResponseTask.addOnFailureListener(exception -> {
            if (exception instanceof ApiException) {
                if (mPlaceList.size() > 0 && mIndex == mCursorDataCount) {
                    //mAdapter.swapPlaces(mPlaceList);
                    //mGeofencing.updateGeofencesList(mPlaceList);
                    //if (mIsEnabled) mGeofencing.registerAllGeofences();
                }
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                Log.e(TAG, "Place not found: " + exception.getMessage() + ", statusCode = " + statusCode);
            }

        });
        mIndex++;
    }
}
