package com.bignerdranch.android.criminalintent;

import android.content.Context;
import android.text.format.DateFormat;

public class CrimeUtils {

    private static final String DATE_FORMAT = "EEE, MMM, dd";

    public static String getCrimeReport(Crime crime, Context context) {
        String solvedString;
        String suspect = "";
        String requiresPolice = "";
        String dateString = DateFormat.format(DATE_FORMAT, crime.getDate()).toString();
        if (crime.isSolved()) {
            solvedString = context.getString(R.string.crime_report_solved);
        } else {
            solvedString = context.getString(R.string.crime_report_unsolved);
        }

        if (crime.getSuspect() == null || crime.getSuspect().isEmpty()) {
            suspect = context.getString(R.string.crime_report_no_suspect);
        } else {
            suspect = context.getString(R.string.crime_report_suspect, crime.getSuspect());
        }

        if (crime.requiresPolice){
            requiresPolice = context.getString(R.string.police_action_required);
            return context.getString(R.string.crime_requires_police_report, crime.getTitle(), dateString, solvedString, requiresPolice, suspect);
        } else {
            return context.getString(R.string.crime_report, crime.getTitle(), dateString, solvedString, suspect);
        }
    }
}