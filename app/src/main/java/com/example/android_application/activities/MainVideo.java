package com.example.android_application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android_application.adapters.AdapterVideo;
import com.example.android_application.databinding.ActivityMainVideoBinding;
import com.example.android_application.models.Video;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

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
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_video);
//        NavigationUI.setupWithNavController(binding.navViewVideo, navController);
//        loadVideosFromFirestore();
        setListeners();
        loadPost();
    }

    private void setListeners(){

        binding.imageVideo.setOnClickListener(v-> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
    }


    private void loadPost(){

        binding.videoRecycleView.setLayoutManager(new LinearLayoutManager(MainVideo.this));
        binding.videoRecycleView.setHasFixedSize(true);

        videoArrayList =  new ArrayList<com.example.android_application.models.Video>();
        db = FirebaseFirestore.getInstance();
        System.out.println("Run database");
        db.collection("videos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshots : task.getResult()){
                                com.example.android_application.models.Video video = new com.example.android_application.models.Video();
                                Map<String, Object> videoData = documentSnapshots.getData();
                                video.setId(videoData.get(Constants.VIDEO_ID).toString());
                                video.setVideoUrl(videoData.get(Constants.VIDEO_URL).toString());
                                video.setTitle(videoData.get(Constants.VIDEO_TITLE).toString());
                                video.setTimestamp(videoData.get(Constants.VIDEO_TIMESTAMP).toString());
                                video.setUser(videoData.get(Constants.VIDEO_CREATOR).toString());

                                videoArrayList.add(video);
                                System.out.println(videoArrayList.toString());

                            }

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        System.out.println(videoArrayList.size());
        adapterVideo = new AdapterVideo(MainVideo.this, videoArrayList, preferenceManager.getString(Constants.NAME));
        binding.videoRecycleView.setAdapter(adapterVideo);
        binding.videoRecycleView.setVisibility(View.VISIBLE);


    }

}