package com.example.charity.utilities;

import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

public class BenefactorsFindingUtils {

    private static final String TAG = BenefactorsFindingUtils.class.getSimpleName() ;

    /**
     *  Finds benefactors places location from google server
     *
     * @param placesClient PlacesClient initialized in Activity
     * @param searchQuery Query entered from user
     */

    public static void findPlacesList(PlacesClient placesClient, String searchQuery) {
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

                    for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                        Log.e(TAG, "onSuccess: " + prediction.getPrimaryText(null).toString());

                    }
                }
        ).addOnFailureListener(exception -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode() + " " + apiException.getStatusMessage());
            }
        });

    }
}
