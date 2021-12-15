package com.bignerdranch.android.criminalintent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.io.File;
import java.util.UUID;

public class CrimeDetailViewModel extends ViewModel {

    private CrimeRepository crimeRepository = CrimeRepository.getINSTANCE();
    private MutableLiveData<UUID> crimeIdLiveData = new MutableLiveData<>();

    LiveData<Crime> crimeLiveData = Transformations.switchMap(crimeIdLiveData, crimeId -> {
        return crimeRepository.getCrime(crimeId);
    });

    void loadCrime(UUID crimeID){
        crimeIdLiveData.setValue(crimeID);
    }

    void saveCrime(Crime crime){
        crimeRepository.updateCrime(crime);
    }

    File getPhotoFile(Crime crime){
        return crimeRepository.getPhotoFile(crime);
    }
}
