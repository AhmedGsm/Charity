package com.example.charity;

/*
* Copyright (C) 2020 Charity android app
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class StoreSearchAdapter extends RecyclerView.Adapter<StoreSearchAdapter.PlaceViewHolder> {

    private Context mContext;
    private List<Place> mStores;

    /**
     * Constructor using the context and the db cursor
     *
     * @param context the calling context/activity
     */
    public StoreSearchAdapter(Context context, List<Place> stores) {
        this.mContext = context;
        this.mStores = stores;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item
     *
     * @param parent   The ViewGroup into which the new View will be added
     * @param viewType The view type of the new View
     * @return A new PlaceViewHolder that holds a View with the item_place_card layout
     */
    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.store_search_item, parent, false);
        return new PlaceViewHolder(view);
    }

    /**
     * Binds the data from a particular position in the cursor to the corresponding view holder
     *
     * @param holder   The PlaceViewHolder instance corresponding to the required position
     * @param position The current position that needs to be loaded with data
     */
    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        String placeName = mStores.get(position).getName();
        String placeAddress = mStores.get(position).getAddress();
        holder.storeNameTextView.setText(placeName);
        holder.storeAddressTextView.setText(placeAddress);
    }

    public void swapStores(List<Place> stores){
        mStores = stores;
        if (mStores != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }


    /**
     * PlaceViewHolder class for the recycler view item
     */
    class PlaceViewHolder extends RecyclerView.ViewHolder {

        TextView storeNameTextView;
        TextView storeAddressTextView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            storeNameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            storeAddressTextView = (TextView) itemView.findViewById(R.id.address_text_view);
        }

    }

    /**
     * Returns the number of items in the cursor
     *
     * @return Number of items in the cursor, or 0 if null
     */
    @Override
    public int getItemCount() {
        if(mStores==null) return 0;
        return mStores.size();
    }
}
