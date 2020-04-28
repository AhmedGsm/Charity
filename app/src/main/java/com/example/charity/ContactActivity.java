package com.example.charity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactActivity extends AppCompatActivity {

    @BindView(R.id.place_name)TextView placeNameTv;
    @BindView(R.id.place_address) TextView placeAddressTv;
    @BindView(R.id.phone_number) TextView phoneNumberTv;
    @BindView(R.id.website) TextView placeWebsiteTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Bind butterKnife library to activity
        ButterKnife.bind(this);

        // Receive intent from details results intent
        Intent currentIntent= getIntent();
        Bundle bundleFromDetailsActivity = currentIntent.getExtras();
        assert bundleFromDetailsActivity != null;
        String placeName = bundleFromDetailsActivity.getString(PlacesDetailsActivity.STRING_KEY_PLACE_NAME);
        String placeAddress = bundleFromDetailsActivity.getString(PlacesDetailsActivity.STRING_KEY_PLACE_ADDRESS);
        String phoneNumber = bundleFromDetailsActivity.getString(PlacesDetailsActivity.STRING_KEY_PLACE_PHONE);
        String website = bundleFromDetailsActivity.getString(PlacesDetailsActivity.STRING_KEY_PLACE_WEBSITE);
        Double longitude = bundleFromDetailsActivity.getDouble(PlacesDetailsActivity.STRING_KEY_PLACE_LONGITUDE);
        Double latitude = bundleFromDetailsActivity.getDouble(PlacesDetailsActivity.STRING_KEY_PLACE_LATITUDE);
        // If website is null replace it with 'None' text inside textView
        placeNameTv.setText(R.string.data_not_available_str);
        if(!TextUtils.isEmpty(placeName)) {
            placeNameTv.setText(placeName);
        }
        // If website is null replace it with 'None' text inside textView
        placeAddressTv.setText(R.string.data_not_available_str);
        if(!TextUtils.isEmpty(placeAddress)) {
            placeAddressTv.setText(placeAddress);
        }
        // If website is null replace it with 'None' text inside textView
        phoneNumberTv.setText(R.string.data_not_available_str);
        if(!TextUtils.isEmpty(phoneNumber)) {
            phoneNumberTv.setText(phoneNumber);
        }
        // If website is null replace it with 'None' text inside textView
        placeWebsiteTv.setText(R.string.data_not_available_str);
        if(!TextUtils.isEmpty(website)) {
            placeWebsiteTv.setText(website);
        }
    }
}
