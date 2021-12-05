package com.bignerdranch.android.criminalintent;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.bignerdranch.android.criminalintent.database.CrimeDao;
import com.bignerdranch.android.criminalintent.database.CrimeDatabase;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CrimeRepository {

    final String DATABASE_NAME = "crime-database";
    private static CrimeRepository INSTANCE;
    private Executor executor = Executors.newSingleThreadExecutor();

    private CrimeDatabase database;
    private CrimeDao crimeDao;

    public static void Initialize(Context context){
        if (INSTANCE == null){
            INSTANCE = new CrimeRepository(context);
        }
    }

    public static CrimeRepository getINSTANCE(){
        if (INSTANCE != null){
            return INSTANCE;
        } else {
            throw new IllegalStateException("CrimeRepository must be initialized");
        }
    }

    private CrimeRepository(Context context){
        database = Room.databaseBuilder(context.getApplicationContext(),CrimeDatabase.class,DATABASE_NAME).addMigrations(CrimeDatabase.MIGRATION_1_2).build();
        crimeDao = database.crimeDao();
    }

    LiveData<List<Crime>> getCrimes(){
        return crimeDao.getCrimes();
    }

    LiveData<Crime> getCrime(UUID id){
        return crimeDao.getCrime(id);
    }

    void updateCrime(Crime crime){
        executor.execute(() -> crimeDao.updateCrime(crime));
    }

    void addCrime(Crime crime){
        executor.execute(() -> crimeDao.addCrime(crime));
    }
}