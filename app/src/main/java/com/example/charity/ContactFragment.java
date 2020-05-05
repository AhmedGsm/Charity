package com.example.charity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    @BindView(R.id.place_name)
    TextView placeNameTv;
    @BindView(R.id.place_address) TextView placeAddressTv;
    @BindView(R.id.phone_number) TextView phoneNumberTv;
    @BindView(R.id.website) TextView placeWebsiteTv;
    @BindView(R.id.phone_call_button)
    Button phoneCallButton;
    @BindView(R.id.send_sms_button) Button sendSmsButton;
    @BindView(R.id.show_location_button) Button showMapButton;
    @BindView(R.id.visit_website_button) Button visitWebsiteButton;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind butterKnife library to activity
        ButterKnife.bind(this,view);

        // Receive Bundle from details Details Fragment
        Bundle bundleFromDetailsFragment = getArguments();
        assert bundleFromDetailsFragment != null;
        String placeName = bundleFromDetailsFragment.getString(DetailsFragment.STRING_KEY_PLACE_NAME);
        String placeAddress = bundleFromDetailsFragment.getString(DetailsFragment.STRING_KEY_PLACE_ADDRESS);
        String phoneNumber = bundleFromDetailsFragment.getString(DetailsFragment.STRING_KEY_PLACE_PHONE);
        String website = bundleFromDetailsFragment.getString(DetailsFragment.STRING_KEY_PLACE_WEBSITE);
        double longitude = bundleFromDetailsFragment.getDouble(DetailsFragment.STRING_KEY_PLACE_LONGITUDE);
        double latitude = bundleFromDetailsFragment.getDouble(DetailsFragment.STRING_KEY_PLACE_LATITUDE);
        // If place name is null replace it with 'None' text inside textView
        placeNameTv.setText(R.string.data_not_available_str);
        if(!TextUtils.isEmpty(placeName)) {
            placeNameTv.setText(placeName);
        }
        // If address is null replace it with 'None' text inside textView
        placeAddressTv.setText(R.string.data_not_available_str);
        if(!TextUtils.isEmpty(placeAddress)) {
            placeAddressTv.setText(placeAddress);
        }
        // If phone number is null replace it with 'None' text inside textView
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
        phoneCallButton.setOnClickListener(viewL -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        // Disable website button if the website link is not supplied
        placeWebsiteTv.setEnabled(true);
        if (website.equals("None")  ) {
            visitWebsiteButton.setEnabled(false);
        }
        // Implement phone call logic
        visitWebsiteButton.setOnClickListener(viewL -> {
            Uri webpage = Uri.parse(website);
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        });

        // Implement Map navigating
        Uri geoLocationUri = Uri.parse("geo:" + latitude + "," + longitude + "?z=5");
        showMapButton.setOnClickListener(viewL -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(geoLocationUri);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);

            }
        });

        // Implement SMS sending
        sendSmsButton.setOnClickListener(viewL -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + phoneNumber));  // This ensures only SMS apps respond
            intent.putExtra("subject", getString(R.string.charity_sms_subject));
            intent.putExtra("sms_body", getString(R.string.charity_sms_message));
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }

        });

    }
}
