package com.aios.jamsession.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aios.jamsession.R;
import com.aios.jamsession.activities.EditProfileActivity;
import com.aios.jamsession.providers.ImageProvider;

import java.io.File;

public class ProfileFragment extends Fragment {

    LinearLayout mLinearLayoutEditProfile;
    View mView;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false);

        mLinearLayoutEditProfile = mView.findViewById(R.id.linearLayoutEditProfile);
        mLinearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });

        return mView;
    }

    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }
}