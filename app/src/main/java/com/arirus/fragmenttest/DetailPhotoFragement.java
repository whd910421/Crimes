package com.arirus.fragmenttest;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.arirus.fragmenttest.Utils.PictureUtils;

import java.io.File;
import java.util.UUID;

/**
 * Created by whd910421 on 16/9/27.
 */
public class DetailPhotoFragement extends DialogFragment {
    private ImageView mImageView;
    private static final String ARG_CRIME_ID = "crime_id";

    public static DetailPhotoFragement newInstance(UUID crimeId) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        DetailPhotoFragement fragment = new DetailPhotoFragement();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.detail_photo_view, null);
        mImageView = (ImageView) v.findViewById(R.id.detail_photo_image);
        UUID crime_id = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

        File mPhotoFiles = CrimeLab.get(getActivity()).getPhotoFile(crime_id);
        if (!mPhotoFiles.exists()) {
            mImageView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFiles.getPath(), getActivity());
            mImageView.setImageBitmap(bitmap);
        }

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("PHOTO DETAIL").
                setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
    }
}
