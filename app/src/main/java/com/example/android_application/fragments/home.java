package com.example.android_application.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.R;
import com.example.android_application.adapters.PostAdapter;
import com.example.android_application.adapters.StoryAdapter;
import com.example.android_application.models.Post;
import com.example.android_application.models.Story;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class home extends Fragment{

    RecyclerView storyRecycl, postRecycler;
    PreferenceManager preferenceManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        preferenceManager = new PreferenceManager(getContext());

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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
                                                    LinearLayoutManager.HORIZONTAL,
                                                                    false);
        storyRecycl.setLayoutManager(layoutManager);

        StoryAdapter storyAdapter = new StoryAdapter(Storys);
        storyRecycl.setAdapter(storyAdapter);


        //============================ Post area


        FirebaseFirestore database = FirebaseFirestore.getInstance();


                database.collection(Constants.COLLECTION_POST)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                                if(error != null){
                                    Log.w("Listen error",error);
                                    return;
                            }
                                for(DocumentChange documentChange : snapshot.getDocumentChanges()){
                                    List<Post> posts = new ArrayList<>();
                                    postRecycler = root.findViewById(R.id.post_recycler);
                                    postRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                                    postRecycler.setHasFixedSize(true);
                                    byte[] bytes = Base64.decode(documentChange.getDocument().getString(Constants.IMAGE), Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    Post post = new Post();

                                    post.postImg = documentChange.getDocument().getString(Constants.POST_IMAGE_URL);
                                    post.date = documentChange.getDocument().getDate(Constants.TIMESTAMP).toString();
                                    post.name = documentChange.getDocument().getString(Constants.NAME);
                                    post.imageProfile = bitmap;
                                    post.title =documentChange.getDocument().getString(Constants.POST_TITLE);
                                    post.description = documentChange.getDocument().getString(Constants.POST_DESCRIPTION);
                                    post.postId = documentChange.getDocument().getId();

                                    if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                                        post.like =documentChange.getDocument().getDouble(Constants.POST_LIKE).intValue() + " likes";
                                        post.comment = documentChange.getDocument().getDouble(Constants.POST_COMMENT).intValue() + " comments";
                                        posts.add(post);
                                    }
                                    PostAdapter postAdapter = new PostAdapter(posts);
                                    postRecycler.setAdapter(postAdapter);

                                }
                            }
                        });

        database.collection(Constants.COLLECTION_POST)
                        .get()
                        .addOnCompleteListener(task -> {

                            if (task.isSuccessful() && task.getResult() != null) {
                                postRecycler = root.findViewById(R.id.post_recycler);
                                postRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
                                postRecycler.setHasFixedSize(true);
                                List<Post> posts = new ArrayList<>();
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                                    byte[] bytes = Base64.decode(queryDocumentSnapshot.getString(Constants.IMAGE), Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    Post post = new Post();
                                    post.postImg = queryDocumentSnapshot.getString(Constants.POST_IMAGE_URL);
                                    post.date = queryDocumentSnapshot.getDate(Constants.TIMESTAMP).toString();
                                    post.name = queryDocumentSnapshot.getString(Constants.NAME);
                                    post.imageProfile = bitmap;
                                    post.title = queryDocumentSnapshot.getString(Constants.POST_TITLE);
                                    post.description = queryDocumentSnapshot.getString(Constants.POST_DESCRIPTION);
                                    post.postId = queryDocumentSnapshot.getId();
                                    post.like = queryDocumentSnapshot.getDouble(Constants.POST_LIKE).intValue() + " likes";
                                    post.comment = queryDocumentSnapshot.getDouble(Constants.POST_COMMENT).intValue() + " comments";
                                    posts.add(post);
                                }
                                PostAdapter postAdapter = new PostAdapter(posts);
                                postRecycler.setAdapter(postAdapter);
                            }
                        });

        return root;
    }


}
