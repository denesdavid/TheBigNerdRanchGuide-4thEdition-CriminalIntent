package com.bignerdranch.android.criminalintent;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Crime {

    //region Fields

    @NonNull
    @PrimaryKey
    public UUID id;
    private String title = "";
    private Date date;
    private boolean isSolved = false;
    public boolean requiresPolice = false;
    private String suspect = "";

    //endregion

    //region Properties

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date _date) {
        this.date = _date;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }

    public boolean RequiresPolice() {
        return requiresPolice;
    }

    public void setRequiresPolice(boolean value) {
        this.requiresPolice = value;
    }

    public String getSuspect() {
        return suspect;
    }

    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }

    //endregion

    //region Constructor
    public Crime () {
        id = UUID.randomUUID();
        date = new Date();
    }
    //endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crime crime = (Crime) o;
        return isSolved == crime.isSolved && requiresPolice == crime.requiresPolice && title.equals(crime.title) && date.equals(crime.date) && suspect.equals(crime.suspect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, date, isSolved, requiresPolice, suspect);
    }

   /* public static final DiffUtil.ItemCallback<Crime> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Crime>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Crime oldCrime, @NonNull Crime newCrime) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return oldCrime.getId() == newCrime.getId();
                }
                @Override
                public boolean areContentsTheSame(
                        @NonNull Crime oldCrime, @NonNull Crime newCrime) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldCrime.equals(newCrime);
                }
            };*/
}