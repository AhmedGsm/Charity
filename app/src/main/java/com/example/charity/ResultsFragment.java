package com.example.charity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.charity.adapters.PlacesSearchAdapter;
import com.example.charity.database.PlaceContract;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Get search benefactors button reference
    @BindView(R.id.stores_list_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.save_stores_list_button)
    Button saveListButton;
    private PlacesSearchAdapter mAdapter;

    public ResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultsFragment.
     */
    public static ResultsFragment newInstance(String param1, String param2) {
        ResultsFragment fragment = new ResultsFragment();
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
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Bind butterKnife library to activity
        ButterKnife.bind(this,view);
        // Receive Bundle from search results Fragment
        Bundle bundleFromSearchFragment= getArguments();
        // set up the recycler view
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PlacesSearchAdapter(getContext(), null);
        mRecyclerView.setAdapter(mAdapter);
        // Display store found in recycler view
        mAdapter.swapStores(bundleFromSearchFragment);
        // Save store ids in content provider
        saveListButton.setOnClickListener(viewL -> {
            saveIdsInContentProvider(bundleFromSearchFragment);
        });
    }

    /**
     * Helper method to save places Ids list in content provider
     */
    private void saveIdsInContentProvider(Bundle placesBundle) {
        ArrayList<String> placesIdList =  placesBundle.getStringArrayList(SearchFragment.PLACES_IDS_EXTRA);
        // Initialize Content values
        ContentValues values = new ContentValues();
        for(int i = 0; i < placesIdList.size(); i++) {
            values.put(PlaceContract.PlaceEntry.COLUMN_PLACE_ID,placesIdList.get(i));
            getActivity().getContentResolver().insert(PlaceContract.PlaceEntry.CONTENT_URI,values);
        }
    }
}
