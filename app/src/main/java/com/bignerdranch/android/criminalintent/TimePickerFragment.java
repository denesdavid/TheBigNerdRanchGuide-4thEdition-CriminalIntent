package com.bignerdranch.android.criminalintent;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment {

    private static final String ARG_TIME= "time";

    public static TimePickerFragment newInstance(Date date){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_TIME, date);
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(bundle);
        return timePickerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Date date = (Date) getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hours = Calendar.HOUR;
        int minutes = Calendar.MINUTE;

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (timePicker, i, i1) -> {
            Date resultDate = new GregorianCalendar(calendar.YEAR, calendar.MONTH, calendar.DAY_OF_MONTH, i, i1).getTime();
            SendResult(resultDate);
        }, hours, minutes, true);

        return timePickerDialog;
    }

    private void SendResult(Date date){
        Bundle result = new Bundle();
        result.putSerializable(CrimeFragment.REQUEST_TIME, date);
        getParentFragmentManager().setFragmentResult(CrimeFragment.REQUEST_TIME, result);
    }
}
