package com.example.charity.adapters;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.charity.R;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlacesDetailsAdapter extends RecyclerView.Adapter<PlacesDetailsAdapter.PlaceViewHolder> {

    @BindView(R.id.place_name) TextView placeNameTv;
    @BindView(R.id.place_address) TextView placeAddressTv;
    @BindView(R.id.phone_number) TextView phoneNumberTv;
    @BindView(R.id.website) TextView placeWebsiteTv;

    private final Context mContext;
    private List<Place> mPlaces;

    static class PlaceViewHolder extends RecyclerView.ViewHolder {
        PlaceViewHolder(CardView CardView) {
            super(CardView);
        }
    }

    public PlacesDetailsAdapter(Context context, List<Place> places) {
        this.mContext = context;
        this.mPlaces = places;
    }

    @NonNull
    @Override
    public PlacesDetailsAdapter.PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
        CardView cardView;
        cardView = (CardView) LayoutInflater.from(mContext).inflate(R.layout.item_details,
                parent,
                false);
        return new PlaceViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesDetailsAdapter.PlaceViewHolder holder,
                                 int position) {
        ButterKnife.bind(this, holder.itemView);
        // If website is null replace it with 'None' text inside textView
        placeNameTv.setText(R.string.data_not_available_str);
        if(!TextUtils.isEmpty(mPlaces.get(position).getName())) {
            placeNameTv.setText(mPlaces.get(position).getName());
        }
        // If website is null replace it with 'None' text inside textView
        placeAddressTv.setText(R.string.data_not_available_str);
        if(!TextUtils.isEmpty(mPlaces.get(position).getAddress())) {
            placeAddressTv.setText(mPlaces.get(position).getAddress());
        }
        // If website is null replace it with 'None' text inside textView
        phoneNumberTv.setText(R.string.data_not_available_str);
        if(!TextUtils.isEmpty(mPlaces.get(position).getPhoneNumber())) {
            phoneNumberTv.setText(mPlaces.get(position).getPhoneNumber());
        }
        // If website is null replace it with 'None' text inside textView
        placeWebsiteTv.setText(R.string.data_not_available_str);
        if(mPlaces.get(position).getWebsiteUri() != null) {
            placeWebsiteTv.setText(Objects.requireNonNull(mPlaces.get(position).getWebsiteUri()).toString());
        }
    }

    @Override
    public int getItemCount() {
        if(mPlaces == null || mPlaces.size() == 0 ) return 0;
        return mPlaces.size();
    }

    public void swapPlaces(List<Place> places) {
        this.mPlaces = places;
        notifyDataSetChanged();
    }

}
