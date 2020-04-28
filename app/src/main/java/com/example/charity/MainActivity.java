package com.example.charity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    // Get search benefactors button reference
    @BindView(R.id.search_benefactor_btn) Button searchBenefactorBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind butterKnife library to activity
        ButterKnife.bind(this);

        // Set click listener for button to go to search activity
        searchBenefactorBtn.setOnClickListener(view -> {
            Intent intentToSearchActivity = new Intent(MainActivity.this, PlacesFindingActivity.class);
            // Start activity with explicit intent
            startActivity(intentToSearchActivity);
        });

    }
}
