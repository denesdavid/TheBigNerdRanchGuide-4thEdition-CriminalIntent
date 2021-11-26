package com.bignerdranch.android.criminalintent;

import android.app.Application;

public class CriminalIntentApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrimeRepository.Initialize(this);
    }
}
