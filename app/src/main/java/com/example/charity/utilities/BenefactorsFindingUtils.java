package com.example.charity.utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.charity.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BenefactorsFindingUtils {

    private static final String TAG = BenefactorsFindingUtils.class.getSimpleName() ;
    private static List<AutocompletePrediction>  mAutocompletePredictions = new ArrayList<>();
    /**
     *  Finds benefactors places location from google server
     *
     * @param placesClient PlacesClient initialized in Activity
     * @param searchQuery Query entered from user
     */

    public static List<AutocompletePrediction> findPlacesList(Context context, PlacesClient placesClient, String searchQuery) {

        // Create a new token for the autocomplete session.
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a dummy RectangularBounds object for testing purposes.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(-33.880490, 151.184363),
                new LatLng(-33.858754, 151.229596));
        // Create a FindAutocompletePredictionsRequest instance.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(bounds)
                .setSessionToken(token)
                .setQuery(searchQuery)
                .build();
        // Set findAutocompletePredictions
        placesClient.findAutocompletePredictions(request).addOnSuccessListener(
                findAutocompletePredictionsResponse -> {
                     mAutocompletePredictions =
                            findAutocompletePredictionsResponse.getAutocompletePredictions();

                }
        ).addOnFailureListener(exception -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, context.getString(R.string.place_not_found_str) + apiException.getStatusCode() + " " + apiException.getStatusMessage());
                Toast.makeText(context, context.getString(R.string.place_not_found_Api_error),Toast.LENGTH_SHORT).show();
            }
        });

        return mAutocompletePredictions;
    }
}
