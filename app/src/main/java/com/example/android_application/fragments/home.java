package com.example.android_application.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.R;
import com.example.android_application.adapters.PostAdapter;
import com.example.android_application.adapters.StoryAdapter;
import com.example.android_application.models.Post;
import com.example.android_application.models.Story;


public class home extends Fragment{

    RecyclerView storyRecycl, postRecycler;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        storyRecycl = root.findViewById(R.id.story_recycler);

        Story[] Storys = new Story[]{
                new Story(R.drawable.dog, "Khaliq"),
                new Story(R.drawable.cat, "Izrail"),
                new Story(R.drawable.hamster, "KdTechs"),
                new Story(R.drawable.dog, "Youtube"),
                new Story(R.drawable.cat, "Example"),
                new Story(R.drawable.hamster, "Testing"),
                new Story(R.drawable.dog, "Youtube"),
                new Story(R.drawable.cat, "Example"),
                new Story(R.drawable.hamster, "Testing"),
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        storyRecycl.setLayoutManager(layoutManager);

        StoryAdapter storyAdapter = new StoryAdapter(Storys);
        storyRecycl.setAdapter(storyAdapter);


        //============================ Post area

        postRecycler = root.findViewById(R.id.post_recycler);
        postRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecycler.setHasFixedSize(true);

        Post[] Posts = new Post[]{
                new Post("Khaliq", R.drawable.dog,R.drawable.dog,"10-12-2021","A Quick brown fox jumps"),
                new Post("Izrail", R.drawable.cat,R.drawable.cat,"10-12-2021","a lazy dog"),
                new Post("KDtechs", R.drawable.hamster,R.drawable.hamster,"10-12-2021","A Quick brown fox jumps"),
                new Post("Khaliq", R.drawable.dog,R.drawable.dog,"10-12-2021","A Quick brown fox jumps"),
                new Post("Izrail", R.drawable.cat,R.drawable.cat,"10-12-2021","a lazy dog"),
                new Post("KDtechs", R.drawable.hamster,R.drawable.hamster,"10-12-2021","A Quick brown fox jumps"),
        };

        PostAdapter adapter = new PostAdapter(Posts);
        postRecycler.setAdapter(adapter);


        return root;
    }
}
