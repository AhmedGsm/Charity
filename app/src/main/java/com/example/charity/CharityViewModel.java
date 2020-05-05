package com.example.charity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.libraries.places.api.model.Place;
import java.util.List;

public class CharityViewModel extends ViewModel {
    MutableLiveData<List<Place>> mPlacesList = new MutableLiveData<>();

    public void setPlacesList(MutableLiveData<List<Place>> placesList) {
        this.mPlacesList = placesList;
    }

    public MutableLiveData<List<Place>> getPlacesList() {
        return mPlacesList;
    }
}
