package com.example.android_application.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.databinding.ActivityProfileAccountPostClickedBinding;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

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
    }
    private void loadPostInfo(){
        Intent intent = getIntent();
        postId = intent.getStringExtra(Constants.POST_IMAGE_ID);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_POST)
                .document(postId)
                .get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful() && task.getResult() != null){
                       DocumentSnapshot documentSnapshot = task.getResult();
                       byte[] bytes = Base64.decode(documentSnapshot.getString(Constants.IMAGE), Base64.DEFAULT);
                       Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                       Picasso.get().load(documentSnapshot.getString(Constants.POST_IMAGE_URL)).into(binding.postImage);
                       binding.profileImage.setImageBitmap(bitmap);
                       binding.profileName.setText(documentSnapshot.getString(Constants.NAME));
                       List<String> userList = (List<String>) documentSnapshot.get(Constants.POST_USER_LIKE);
                       Integer likeLength = userList.size();
                       binding.feedsLikesCount.setText(likeLength.toString() + " likes");
                       binding.profileDate.setText(documentSnapshot.getDate(Constants.TIMESTAMP).toString());
                       binding.postTitle.setText(documentSnapshot.getString(Constants.POST_TITLE));
                       binding.postDescription.setText(documentSnapshot.getString(Constants.POST_DESCRIPTION));
                       if(userList.contains(preferenceManager.getString(Constants.USER_ID))){
                           binding.feedsPostLike.setVisibility(View.GONE);
                           binding.feedsPostLiked.setVisibility(View.VISIBLE);
                       }
                   }
                });
    }
}