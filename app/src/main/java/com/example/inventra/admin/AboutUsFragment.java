package com.example.inventra.admin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.inventra.R;

public class AboutUsFragment extends Fragment {

    private ImageView linkedinImage, githubImage, twitterImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about_us, container, false);

        // Initialize ImageViews
        linkedinImage = rootView.findViewById(R.id.linkedinId);
        githubImage = rootView.findViewById(R.id.githubId);
        twitterImage = rootView.findViewById(R.id.twitterId);

        // Set click listeners for social media icons
        linkedinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSocialMediaLink("https://www.linkedin.com/in/prakash-sirvi/");
            }
        });

        githubImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSocialMediaLink("https://github.com/prakash0067/");
            }
        });

        twitterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSocialMediaLink("https://twitter.com/Prakash35277492/");
            }
        });

        return rootView;
    }

    // Method to open social media link
    private void openSocialMediaLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}