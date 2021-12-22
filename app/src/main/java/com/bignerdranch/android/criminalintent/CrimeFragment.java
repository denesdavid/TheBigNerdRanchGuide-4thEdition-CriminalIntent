package com.bignerdranch.android.criminalintent;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.format.DateFormat;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    //region Fields

    public final static String REQUEST_DATE = "request_date";
    public final static String REQUEST_TIME = "request_time";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DATE_FORMAT = "EEE, MMM, dd";
    private final String DIALOG_DATE = "DialogDate";
    private final String DIALOG_TIME = "DialogTime";
    private final String DIALOG_PHOTO = "DialogPhoto";
    ActivityResultLauncher<Void> contactResultLauncher;
    ActivityResultLauncher<String> requestContactsPermissionLauncher;
    ActivityResultLauncher<Uri> cameraResultLauncher;
    private Crime crime;
    private File photoFile;
    private Uri photoUri;
    private EditText titleField;
    private Button dateButton;
    private Button timeButton;
    private CheckBox solvedCheckBox;
    private CheckBox requiresPoliceCheckBox;
    private Button reportButton;
    private Button suspectButton;
    private Button callSuspectButton;
    private ImageButton photoButton;
    private ImageView photoView;
    private CrimeDetailViewModel crimeDetailViewModel;
    private ViewTreeObserver viewTreeObserver;

    //endregion

    //region Lifecycle events

    public static CrimeFragment newInstance(UUID crimeID) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_CRIME_ID, crimeID);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(bundle);
        return crimeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        crime = new Crime();
        UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        crimeDetailViewModel = new ViewModelProvider(this).get(CrimeDetailViewModel.class);
        crimeDetailViewModel.loadCrime(crimeID);

        contactResultLauncher = registerForActivityResult(new ActivityResultContracts.PickContact(), result -> {
            if (result != null){
                setSuspect(result);
            }
        });

        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher, as an instance variable.
        requestContactsPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                callSuspect();
                // Permission is granted. Continue the action or workflow in your
                // app.
            } else {
                requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
            }
        });

        cameraResultLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            if (result){
                updatePhotoView();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime, container, false);
        titleField = view.findViewById(R.id.crime_title);
        dateButton = view.findViewById(R.id.crime_date);
        timeButton = view.findViewById(R.id.crime_time);
        solvedCheckBox = view.findViewById(R.id.crime_solved);
        requiresPoliceCheckBox = view.findViewById(R.id.requires_police);
        reportButton = view.findViewById(R.id.crime_report);
        suspectButton = view.findViewById(R.id.crime_suspect);
        callSuspectButton = view.findViewById(R.id.call_suspect);
        photoButton = view.findViewById(R.id.crime_camera);
        photoView = view.findViewById(R.id.crime_photo);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        crimeDetailViewModel.crimeLiveData.observe(
                getViewLifecycleOwner(),
                crime -> {
                    this.crime = crime;
                    photoFile = crimeDetailViewModel.getPhotoFile(crime);
                    photoUri = FileProvider.getUriForFile(requireContext(),
                            "com.bignerdranch.android.criminalintent.fileprovider",
                            photoFile);
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

        solvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            crime.setSolved(isChecked);
            requiresPoliceCheckBox.setEnabled(!isChecked);
            if (isChecked) {
                requiresPoliceCheckBox.setChecked(false);
            }
        });

        requiresPoliceCheckBox.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            crime.setRequiresPolice(isChecked);
            if (isChecked) {
                solvedCheckBox.setChecked(false);
            }
        }));

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

        reportButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
            intent = Intent.createChooser(intent, getString(R.string.send_report));
            startActivity(intent);
        });

        suspectButton.setOnClickListener(view -> contactResultLauncher.launch(null));

        callSuspectButton.setOnClickListener(view -> {
            if (crime.getSuspect() != null && !crime.getSuspect().isEmpty()){
                requestContactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
            } else {
                Toast.makeText(requireContext(), "Contact is not presented at this crime.", Toast.LENGTH_LONG).show();
            }
        });

        photoButton.setOnClickListener(view -> {
            PackageManager packageManager = requireContext().getPackageManager();
            try {
                if (((CameraManager)requireContext().getSystemService(Context.CAMERA_SERVICE)).getCameraIdList().length > 0 ) {
                    cameraResultLauncher.launch(photoUri);
                } else {
                    Toast.makeText(requireContext(), "Camera is not presented on this device.", Toast.LENGTH_LONG).show();
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        });

        photoView.setOnClickListener(view -> {
            PhotoDetailFragment.newInstance(photoFile.getPath()).show(getParentFragmentManager(), DIALOG_PHOTO);
        });


        photoView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            updatePhotoView();
        });


    }

    @Override
    public void onStop() {
        super.onStop();
        crimeDetailViewModel.saveCrime(crime);
    }


    //endregion

    //region Methods

    void updateUI() {
        titleField.setText(crime.getTitle());
        SimpleDateFormat dt = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.US);
        SimpleDateFormat dt2 = new SimpleDateFormat("HH:mm", Locale.US);
        dateButton.setText(dt.format(this.crime.getDate()));
        timeButton.setText(dt2.format(this.crime.getDate()));
        solvedCheckBox.setChecked(crime.isSolved());
        requiresPoliceCheckBox.setChecked(crime.requiresPolice);

        if (crime.getSuspect() != null && !crime.getSuspect().isEmpty()) {
            suspectButton.setText(crime.getSuspect());
        } else {
            suspectButton.setText(R.string.crime_suspect_text);
        }
        updatePhotoView();

        solvedCheckBox.jumpDrawablesToCurrentState();
        requiresPoliceCheckBox.jumpDrawablesToCurrentState();
    }

    void updatePhotoView(){
        if (photoFile != null){
            if (photoFile.exists()){
                Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), photoView.getWidth(), photoView.getHeight());
                photoView.setImageBitmap(bitmap);
            } else {
                photoView.setImageDrawable(null);
            }
        }
    }

    void setSuspect(Uri contactUri) {
        Cursor cursor = requireActivity().getContentResolver().query(contactUri,
                new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                null, null, null);
        if (cursor.getCount() == 0) {
            return;
        }
        cursor.moveToFirst();
        String suspect = cursor.getString(0);
        cursor.close();

        crime.setSuspect(suspect);
        crimeDetailViewModel.saveCrime(crime);
        suspectButton.setText(suspect);
    }

    void callSuspect(){
        ContentResolver contentResolver = requireActivity().getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = " + "\"" +crime.getSuspect() + "\"",
                null,
                null);
        cursor.moveToFirst();
        String number = "";
        int index = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA1);
        if (index > -1){
            number = cursor.getString(index);
        }

        cursor.close();

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+number));
        startActivity(callIntent);
    }

    private String getCrimeReport() {
        String solvedString;
        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateString = DateFormat.format(DATE_FORMAT, crime.getDate()).toString();
        String suspect = "";

        if (crime.getSuspect() == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            getString(R.string.crime_report_suspect, crime.getSuspect());
        }

        return getString(R.string.crime_report, crime.getTitle(), dateString, solvedString, suspect);
    }

    //endregion

}