package com.example.charity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.net.Proxy.Type.HTTP;

public class ContactActivity extends AppCompatActivity {

    @BindView(R.id.place_name)TextView placeNameTv;
    @BindView(R.id.place_address) TextView placeAddressTv;
    @BindView(R.id.phone_number) TextView phoneNumberTv;
    @BindView(R.id.website) TextView placeWebsiteTv;
    @BindView(R.id.phone_call_button) Button phoneCallButton;
    @BindView(R.id.send_sms_button) Button sendSmsButton;
    @BindView(R.id.show_location_button) Button showMapButton;
    @BindView(R.id.visit_website_button) Button visitWebsiteButton;

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
        double longitude = bundleFromDetailsActivity.getDouble(PlacesDetailsActivity.STRING_KEY_PLACE_LONGITUDE);
        double latitude = bundleFromDetailsActivity.getDouble(PlacesDetailsActivity.STRING_KEY_PLACE_LATITUDE);
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
        // Disable call & SMS button if the phone number is not supplied
        phoneCallButton.setEnabled(true);
        sendSmsButton.setEnabled(true);
        if (phoneNumber.equals("None")  ) {
            phoneCallButton.setEnabled(false);
            sendSmsButton.setEnabled(false);
        }
        // Implement phone call logic
        phoneCallButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        // Disable website button if the website link is not supplied
        placeWebsiteTv.setEnabled(true);
        if (website.equals("None")  ) {
            visitWebsiteButton.setEnabled(false);
        }
        // Implement phone call logic
        visitWebsiteButton.setOnClickListener(view -> {
            Uri webpage = Uri.parse(website);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        // Implement Map navigating
        Uri geoLocationUri = Uri.parse("geo:" + latitude + "," + longitude + "?z=5");
        showMapButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoLocationUri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);

            }
        });

        // Implement SMS sending
        sendSmsButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + phoneNumber));  // This ensures only SMS apps respond
            intent.putExtra("subject", getString(R.string.charity_sms_subject));
            intent.putExtra("sms_body", getString(R.string.charity_sms_message));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }

        });


    }
}
