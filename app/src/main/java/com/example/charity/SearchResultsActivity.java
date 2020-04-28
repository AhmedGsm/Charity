package com.example.charity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.charity.database.PlaceContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultsActivity extends AppCompatActivity {

    // Get search benefactors button reference
    @BindView(R.id.stores_list_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.save_stores_list_button) Button  saveListButton;
    private StoreSearchAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        // Bind butterKnife library to activity
        ButterKnife.bind(this);

        // Receive intent from search results intent
        Intent currentIntent= getIntent();
        Bundle bundleFromSearchResultsActivity = currentIntent.getExtras();
        int sizebundle = bundleFromSearchResultsActivity.size();
        // set up the recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new StoreSearchAdapter(SearchResultsActivity.this, null);
        mRecyclerView.setAdapter(mAdapter);
        // Display store found in recycler view
        mAdapter.swapStores(bundleFromSearchResultsActivity);

        // Save store ids in content provider
        saveListButton.setOnClickListener(view -> {
            saveIdsInContentProvider(bundleFromSearchResultsActivity);
        });
    }

    /**
     * Helper method to save store Ids list to content provider
     */
    private void saveIdsInContentProvider(Bundle placesBundle) {
        ArrayList<String> placesIdList =  placesBundle.getStringArrayList(PlacesFindingActivity.PLACES_IDS_EXTRA);
        // Initialize Content values
        ContentValues values = new ContentValues();
        for(int i = 0; i < placesIdList.size(); i++) {
            values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID,placesIdList.get(i));
            getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI,values);
        }
    }
}
