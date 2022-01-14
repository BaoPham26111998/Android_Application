package com.example.android_application.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.adapters.AdapterVideo;
import com.example.android_application.adapters.PostAdapter;
import com.example.android_application.databinding.FragmentVideoBinding;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;


public class Video extends Fragment  {

    RecyclerView storyRecycl, postRecycler;
    PreferenceManager preferenceManager;
    private PostAdapter postAdapter;
    private ArrayList<com.example.android_application.models.Video> videoArrayList;
    FirebaseFirestore db;
    AdapterVideo adapterVideo;
    private FragmentVideoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVideoBinding.inflate(inflater,container,false);
        preferenceManager = new PreferenceManager(getContext());
        View view = binding.getRoot();





        //============================ Post area
        loadPost();
        setListener();

        return view;
    }
    private void setListener(){

    }


    private void loadPost(){

        postRecycler = binding.videoRecycler;
        postRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecycler.setHasFixedSize(true);

        videoArrayList =  new ArrayList<com.example.android_application.models.Video>();
        db = FirebaseFirestore.getInstance();

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
                                System.out.println(videoArrayList.size());
                            }

                        }
                        else{
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        adapterVideo = new AdapterVideo(getContext(), videoArrayList, preferenceManager.getString(Constants.NAME));
        binding.videoRecycler.setAdapter(adapterVideo);
        binding.videoRecycler.setVisibility(View.VISIBLE);


    }


}
