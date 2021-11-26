package com.bignerdranch.android.criminalintent.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.bignerdranch.android.criminalintent.Crime;

import java.util.List;
import java.util.UUID;

@Dao
public interface CrimeDao {

    @Query("SELECT * FROM crime")
    LiveData<List<Crime>> getCrimes();

    @Query("SELECT * FROM crime WHERE id=(:id)")
    LiveData<Crime> getCrime(UUID id);

    @Update
    void updateCrime(Crime crime);

    @Insert
    void addCrime(Crime crime);
}