package com.example.android_application.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.android_application.R;
import com.example.android_application.adapters.AdapterVideo;
import com.example.android_application.databinding.ActivityMainVideoBinding;
import com.example.android_application.models.Video;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainVideo extends AppCompatActivity {
    ActivityMainVideoBinding binding;
    private ArrayList<Video> videoArrayList;
    FirebaseFirestore db;
    AdapterVideo adapterVideo;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainVideoBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());



        setContentView(binding.getRoot());
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_video);
        NavigationUI.setupWithNavController(binding.navViewVideo, navController);
//        loadVideosFromFirestore();
        setListeners();
    }

    private void setListeners(){

        binding.imageVideo.setOnClickListener(v-> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
    }

//
//
//    }
}