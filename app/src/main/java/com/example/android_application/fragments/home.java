package com.example.android_application.fragments;

import android.content.Intent;
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
import com.example.android_application.activities.AccountPostLikedList;
import com.example.android_application.activities.AccountProfileActivity;
import com.example.android_application.activities.PostAccountProfileActivity;
import com.example.android_application.activities.PostCommentList;
import com.example.android_application.adapters.PostAdapter;
import com.example.android_application.adapters.StoryAdapter;
import com.example.android_application.databinding.FragmentHomeBinding;
import com.example.android_application.listeners.PostListener;
import com.example.android_application.models.Post;
import com.example.android_application.models.Story;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class home extends Fragment implements PostListener {

    RecyclerView storyRecycl, postRecycler;
    PreferenceManager preferenceManager;
    private PostAdapter postAdapter;
    List<Post> posts = new ArrayList<>();
    ArrayList<String> userIdList = new ArrayList<>();
    ArrayList<Object> userCommentList = new ArrayList<>();
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        preferenceManager = new PreferenceManager(getContext());
        storyRecycl = binding.storyRecycler;
        View view = binding.getRoot();


        // ================= Story Area


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
        loadPost();
        setListener();

        return view;
    }
    private void setListener(){

    }


    private void loadPost(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        postRecycler = binding.postRecycler;
        postRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        postRecycler.setHasFixedSize(true);


        // Retrieve data from firebase

        database.collection(Constants.COLLECTION_POST)
                // Data snapshot listener to recognize the data changed from firebase
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.w("Listen error",error);
                            return;
                        }

                        for(DocumentChange documentChange : snapshot.getDocumentChanges()){
                            //When new post was add
                            if(documentChange.getType() == DocumentChange.Type.ADDED) {
                                byte[] bytes = Base64.decode(documentChange.getDocument().getString(Constants.IMAGE), Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                Post post = new Post();
                                post.postImg = documentChange.getDocument().getString(Constants.POST_IMAGE_URL);
                                post.postVideo = documentChange.getDocument().getString("postVideoUrl");
                                post.date = documentChange.getDocument().getDate(Constants.TIMESTAMP).toString();
                                post.name = documentChange.getDocument().getString(Constants.NAME);
                                post.imageProfile = bitmap;
                                post.title = documentChange.getDocument().getString(Constants.POST_TITLE);
                                post.description = documentChange.getDocument().getString(Constants.POST_DESCRIPTION);
                                post.postId = documentChange.getDocument().getId();
                                post.userId = documentChange.getDocument().getString(Constants.USER_ID);
                                post.userIdList = (ArrayList<String>) documentChange.getDocument().get(Constants.POST_USER_LIKE);
                                post.userCommentList =  documentChange.getDocument().getLong(Constants.POST_COMMENT).intValue();
                                Integer likeLength = userIdList.size();
                                post.likeCount = likeLength.toString() +" likes";
//                                post.comment = documentChange.getDocument().co.intValue() + " comments";
                                posts.add(post);
                            }
                            //When post was update or change
                            if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                                List<String> userIdList = (List<String>) documentChange.getDocument().get(Constants.POST_USER_LIKE);
                                Integer commentCount = documentChange.getDocument().getLong(Constants.POST_COMMENT).intValue();
                                Integer likeLength = userIdList.size();
                                String imageString = documentChange.getDocument().getString(Constants.IMAGE);
                                System.out.println(posts.size());
                                for(int i=0; i<posts.size();i++){
                                    String postId = documentChange.getDocument().getId();
                                    if(posts.get(i).postId.equals(postId)){
                                        System.out.println(postId);
                                        posts.get(i).comment = commentCount.toString();
                                        posts.get(i).likeCount = likeLength.toString() +" likes";
                                        byte[] bytes = Base64.decode(imageString, Base64.DEFAULT);
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        posts.get(i).imageProfile = bitmap;
                                        System.out.println(posts.get(i).likeCount);

                                        break;
                                    }

                                }
                            }

                        }


                                postAdapter.notifyDataSetChanged();
                    }
                });


            postAdapter = new PostAdapter(posts, this);
            postRecycler.setAdapter(postAdapter);
    }

    @Override
    public void onImageProfileClicked(Post post) {
        String postId = post.postId;
        String userId = post.userId;
        if(userId.equals(preferenceManager.getString(Constants.USER_ID))){
            Intent intent = new Intent(getActivity(), AccountProfileActivity.class)
                    .putExtra(Constants.POST_IMAGE_ID,postId)
                    .putExtra(Constants.USER_ID,userId);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(getActivity(), PostAccountProfileActivity.class)
                    .putExtra(Constants.POST_IMAGE_ID,postId)
                    .putExtra(Constants.USER_ID,userId);
            startActivity(intent);
        }

    }

    @Override
    public void onLikedCountClicked(Post post) {
        System.out.println(post.userIdList.size());
        Intent intent = new Intent(getActivity(), AccountPostLikedList.class)
                .putExtra(Constants.POST_USER_LIKE,post.userIdList );
        startActivity(intent);
    }

    @Override
    public void onCommnentClicked(Post post) {
        Intent intent = new Intent(getActivity(), PostCommentList.class)
                .putExtra(Constants.POST_IMAGE_ID,post.postId);
        startActivity(intent);
    }



}
