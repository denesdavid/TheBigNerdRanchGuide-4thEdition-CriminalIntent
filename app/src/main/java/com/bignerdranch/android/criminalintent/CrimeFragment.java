package com.bignerdranch.android.criminalintent;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    //region Fields

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private Button timeButton;
    private CheckBox solvedCheckBox;
    private final String TAG = "CrimeFragment";
    private final String DIALOG_DATE= "DialogDate";
    private final String DIALOG_TIME= "DialogTime";
    public final static String REQUEST_DATE = "request_date";
    public final static String REQUEST_TIME = "request_time";
    private static final String ARG_CRIME_ID = "crime_id";
    private CrimeDetailViewModel crimeDetailViewModel;

    //endregion

    //region Lifecycle events

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        crimeDetailViewModel = new ViewModelProvider(this).get(CrimeDetailViewModel.class);

        crime = new Crime();
        UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        crimeDetailViewModel.loadCrime(crimeID);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        titleField = view.findViewById(R.id.crime_title);
        dateButton = view.findViewById(R.id.crime_date);
        timeButton = view.findViewById(R.id.crime_time);
        solvedCheckBox = view.findViewById(R.id.crime_solved);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        crimeDetailViewModel.crimeLiveData.observe(
                getViewLifecycleOwner(),
                crime -> {
                    this.crime = crime;
                    updateUI();
                }
        );
    }

    @Override
    public void onStart() {
        super.onStart();

        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        solvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> crime.setSolved(isChecked));

        dateButton.setOnClickListener(view -> {
            DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(crime.getDate());
            getParentFragmentManager().setFragmentResultListener(REQUEST_DATE, getViewLifecycleOwner(), (requestKey, result) -> {
                crime.setDate((Date) result.get(REQUEST_DATE));
                updateUI();
            });
            datePickerFragment.show(getParentFragmentManager(), DIALOG_DATE);
        });

        timeButton.setOnClickListener(view -> {
            TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(crime.getDate());
            getParentFragmentManager().setFragmentResultListener(REQUEST_TIME, getViewLifecycleOwner(), (requestKey, result) -> {
                crime.setDate((Date) result.get(REQUEST_TIME));
                updateUI();
            });
            timePickerFragment.show(getParentFragmentManager(), DIALOG_TIME);
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        crimeDetailViewModel.saveCrime(crime);
    }

    //endregion

    void updateUI(){
        titleField.setText(crime.getTitle());
        SimpleDateFormat dt = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.US);
        SimpleDateFormat dt2 = new SimpleDateFormat("H:m", Locale.US);
        dateButton.setText(dt.format(this.crime.getDate()));
        //dateButton.setText(crime.getDate().toString());
        timeButton.setText(dt2.format(this.crime.getDate()));
        solvedCheckBox.setChecked(crime.isSolved());
        solvedCheckBox.jumpDrawablesToCurrentState();
    }

    public static CrimeFragment newInstance(UUID crimeID){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CRIME_ID, crimeID);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(bundle);
        return crimeFragment;
    }

}
