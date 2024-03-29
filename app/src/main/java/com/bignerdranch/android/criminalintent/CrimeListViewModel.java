package com.bignerdranch.android.criminalintent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class CrimeListViewModel extends ViewModel {

    LiveData<List<Crime>> crimeListLiveData;
    private CrimeRepository crimeRepository;

    public CrimeListViewModel () {
        crimeRepository = CrimeRepository.getINSTANCE();
        crimeListLiveData = crimeRepository.getCrimes();
    }

    public void addCrime(Crime crime){
        crimeRepository.addCrime(crime);
    }
}