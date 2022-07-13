package com.example.makemeals.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.makemeals.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvProfileUsername = view.findViewById(R.id.tvProfileUsername);
        ImageView ivProfileImage = view.findViewById(R.id.ivProfileImage);

        tvProfileUsername.setText(ParseUser.getCurrentUser().getUsername());

        String PROFILE_IMAGE = "profileImage";
        ParseFile image = ParseUser.getCurrentUser().getParseFile(PROFILE_IMAGE);

        if (image != null) {
            Glide.with(requireContext()).load(image.getUrl())
                    .circleCrop()
                    .into(ivProfileImage);
        }
    }
}