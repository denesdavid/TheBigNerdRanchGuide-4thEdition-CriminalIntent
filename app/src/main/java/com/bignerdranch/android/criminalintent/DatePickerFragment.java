package com.bignerdranch.android.criminalintent;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentResultOwner;

import java.time.Year;
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
        int initialYear = calendar.get(calendar.YEAR);
        int initialMonth = calendar.get(calendar.MONTH);
        int initialDay = calendar.get(calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), null, initialYear, initialMonth, initialDay);


        datePickerDialog.setOnDateSetListener((datePicker, i, i1, i2) -> {
            Date resultDate = new GregorianCalendar(i, i1, i2).getTime();
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