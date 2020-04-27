package com.example.charity;

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

import butterknife.BindView;
import butterknife.ButterKnife;

public class BenefactorsFindingActivity extends AppCompatActivity {

    private static final String TAG = BenefactorsFindingActivity.class.getSimpleName();
    private static final String SEARCH_QUERY = "search_query" ;

    // Get search view reference
    @BindView(R.id.search_view_for_benefactors) SearchView mSearchView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benefactors_finding);

        // Bind butterKnife library to activity
        ButterKnife.bind(this);
        handleIntent(getIntent());

        // Setup the search view
        SearchManager searchManager =(SearchManager) getSystemService(
                Context.SEARCH_SERVICE);
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        mSearchView.setSubmitButtonEnabled(true);
         mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intentToCurrentActivity = new Intent(BenefactorsFindingActivity.this, BenefactorsFindingActivity.class);
                intentToCurrentActivity.setAction(Intent.ACTION_SEARCH);
                intentToCurrentActivity.putExtra(SEARCH_QUERY,query);
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

        }
    }

}
