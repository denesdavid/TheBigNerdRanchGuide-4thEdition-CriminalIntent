package com.bignerdranch.android.criminalintent.database;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import java.util.Date;
import java.util.UUID;

public class CrimeTypeConverters {

    @NonNull
    @TypeConverter
    public long fromDate(Date date){
        return date.getTime();
    }

    @NonNull
    @TypeConverter
    public Date toDate(long millisSinceEpoch){
        return new Date(millisSinceEpoch);
    }

    @NonNull
    @TypeConverter
    public UUID toUUID(String uuid){
        return UUID.fromString(uuid);
    }

    @NonNull
    @TypeConverter
    public String fromUUID(UUID uuid){
        return uuid.toString();
    }
}