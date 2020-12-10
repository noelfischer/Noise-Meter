package ch.zli.noiselevelmeter;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {

    private static MutableLiveData<Integer> mutableLiveData = new MutableLiveData<>();

    public static void setLiveData(int mutableLiveData) {
        MyViewModel.mutableLiveData.postValue(mutableLiveData);
    }

    public static MutableLiveData<Integer> getLiveData() {
        return mutableLiveData;
    }

    public static Integer getValue() {
        return mutableLiveData.getValue();
    }
}
