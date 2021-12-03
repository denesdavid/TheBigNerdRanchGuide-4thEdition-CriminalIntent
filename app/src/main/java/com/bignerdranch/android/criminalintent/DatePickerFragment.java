package com.bignerdranch.android.criminalintent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";

    public static DatePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE, date);
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(bundle);
        return datePickerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Date date = (Date) getArguments().getSerializable(ARG_DATE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int initialYear = calendar.get(Calendar.YEAR);
        int initialMonth = calendar.get(Calendar.MONTH);
        int initialDay = calendar.get(Calendar.DAY_OF_MONTH);
        int initialHour = calendar.get(Calendar.HOUR_OF_DAY);
        int initialMinute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), null, initialYear, initialMonth, initialDay);


        datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {
            Date resultDate = new GregorianCalendar(i, i1, i2, initialHour, initialMinute).getTime();
            SendResult(resultDate);
        });

        return datePickerDialog;
    }

    private void SendResult(Date date){
        Bundle result = new Bundle();
        result.putSerializable(CrimeFragment.REQUEST_DATE, date);
        getParentFragmentManager().setFragmentResult(CrimeFragment.REQUEST_DATE, result);
    }
}