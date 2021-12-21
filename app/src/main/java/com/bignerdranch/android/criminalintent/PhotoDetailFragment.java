package com.bignerdranch.android.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PhotoDetailFragment extends DialogFragment {

    private static final String ARG_PHOTO = "path";

    public static PhotoDetailFragment newInstance(String photoPath) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_PHOTO, photoPath);
        PhotoDetailFragment photoDetailFragment = new PhotoDetailFragment();
        photoDetailFragment.setArguments(bundle);
        return photoDetailFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String photoPath = (String) getArguments().getSerializable(ARG_PHOTO);
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.photo_detail_dialog, null);
        ImageView imageView = view.findViewById(R.id.imageview_photo_detail);
        imageView.setImageBitmap(bitmap);

        imageView.setOnClickListener(v -> this.dismiss());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        return builder.create();
    }
}