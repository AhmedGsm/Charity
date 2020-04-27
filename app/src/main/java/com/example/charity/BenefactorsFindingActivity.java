package com.example.charity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.charity.utilities.BenefactorsFindingUtils;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BenefactorsFindingActivity extends AppCompatActivity {

    private static final String TAG = BenefactorsFindingActivity.class.getSimpleName();
    private static final String SEARCH_QUERY = "search_query";

    // Get search view reference
    @BindView(R.id.search_view_for_benefactors)
    SearchView mSearchView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    private PlacesClient mPlacesClient;
    //Google account Api key
    private String API_KEY1 = "AIzaSyCeOX-6wnF-i2hiWcwFRUWuqt4Cgbib7KA";
    private String API_KEY2 = "AIzaSyBDTy_VMG4yVBe36tWMftmO-kXmjpenODg";

    private String API_KEY = API_KEY2;
    private int i = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefactors_finding);
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
                Intent intentToCurrentActivity = new Intent(BenefactorsFindingActivity.this, BenefactorsFindingActivity.class);
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
            // Find place list
            List<AutocompletePrediction> placesFound = BenefactorsFindingUtils.findPlacesList(this, mPlacesClient, searchQuery);
            if(placesFound != null && placesFound.size() > 0) {
                Log.i(TAG, "Places found log: " + placesFound);
                // Go to listing places activity
                Intent intentToDetailsPlacesActivity = new Intent(BenefactorsFindingActivity.this, SearchResultsActivity.class );
                startActivity(intentToDetailsPlacesActivity);
            } else {
                Log.i(TAG, getString(R.string.place_not_found_str));
                Toast.makeText(this, getString(R.string.place_not_found_str),Toast.LENGTH_SHORT).show();
            }
            // Display search view and hide progress
            mProgressBar.setVisibility(View.GONE);
            mSearchView.setVisibility(View.VISIBLE);
        }
    }

}
