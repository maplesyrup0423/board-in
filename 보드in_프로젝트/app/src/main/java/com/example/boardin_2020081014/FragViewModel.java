package com.example.boardin_2020081014;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// ViewModel을 상속받는 FragViewModel 클래스
public class FragViewModel extends ViewModel {
    private final MutableLiveData<GameData> liveData = new MutableLiveData<>();

    public LiveData<GameData> getLiveData() {
        return liveData;
    }

    public  void setLiveData(GameData gameData) {
        liveData.setValue(gameData);
    }
}
