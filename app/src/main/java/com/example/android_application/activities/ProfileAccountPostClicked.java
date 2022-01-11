package com.example.android_application.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.databinding.ActivityProfileAccountPostClickedBinding;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileAccountPostClicked extends AppCompatActivity {
    ActivityProfileAccountPostClickedBinding binding;
    String postId;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileAccountPostClickedBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        loadPostInfo();
        setListeners();

    }

    private void setListeners(){
        binding.returnImage.setOnClickListener(v -> onBackPressed());
        binding.feedsPostLiked.setOnClickListener(v -> {

            unLiked(postId);
            binding.feedsPostLike.setVisibility(View.VISIBLE);
            binding.feedsPostLiked.setVisibility(View.GONE);
        });
        binding.feedsPostLike.setOnClickListener(v -> {
            liked(postId);
            binding.feedsPostLike.setVisibility(View.GONE);
            binding.feedsPostLiked.setVisibility(View.VISIBLE);
        });
    }

    private void loadPostInfo(){
        Intent intent = getIntent();
        postId = intent.getStringExtra(Constants.POST_IMAGE_ID);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_POST)
                .document(postId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException error) {
                        if (snapshot.exists()){
                            byte[] bytes = Base64.decode(snapshot.getString(Constants.IMAGE), Base64.DEFAULT);
                       Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                       Picasso.get().load(snapshot.getString(Constants.POST_IMAGE_URL)).into(binding.postImage);
                       binding.profileImage.setImageBitmap(bitmap);
                       binding.profileName.setText(snapshot.getString(Constants.NAME));
                       List<String> userList = (List<String>) snapshot.get(Constants.POST_USER_LIKE);
                       Integer likeLength = userList.size();
                       binding.feedsLikesCount.setText(likeLength.toString() + " likes");
                       binding.profileDate.setText(snapshot.getDate(Constants.TIMESTAMP).toString());
                       binding.postTitle.setText(snapshot.getString(Constants.POST_TITLE));
                       binding.postDescription.setText(snapshot.getString(Constants.POST_DESCRIPTION));
                       if(userList.contains(preferenceManager.getString(Constants.USER_ID))){
                           binding.feedsPostLike.setVisibility(View.GONE);
                           binding.feedsPostLiked.setVisibility(View.VISIBLE);
                       }
                        }
                    }
                });

    }

    private void liked(String postId){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        ArrayList list = new ArrayList();
        list.add(preferenceManager.getString(Constants.USER_ID));
        database.collection(Constants.COLLECTION_POST)
                .document(postId)
                .update("userLiked", FieldValue.arrayUnion(list.toArray()));
    }

    private void unLiked(String postId){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        ArrayList list = new ArrayList();
        list.add(preferenceManager.getString(Constants.USER_ID));
        database.collection(Constants.COLLECTION_POST)
                .document(postId)
                .update("userLiked", FieldValue.arrayRemove(list.toArray()));
    }
}