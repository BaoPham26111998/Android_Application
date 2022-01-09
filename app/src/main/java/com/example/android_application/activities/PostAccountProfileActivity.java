package com.example.android_application.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.adapters.ProfileAccountPostsAdapter;
import com.example.android_application.databinding.ActivityPostAccountProfileBinding;
import com.example.android_application.models.Post;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostAccountProfileActivity extends AppCompatActivity {
    ActivityPostAccountProfileBinding binding;
    List<String> images = new ArrayList<String>();
    List<Post> posts = new ArrayList<>();
    private String userId;

    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityPostAccountProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.USER_ID);

        setListeners();
        loadUserInfo();
        setPhotoGridView();

    }

    private void setListeners(){
        //log out by click in to the icon on the top right corner
        binding.backButton.setOnClickListener(v -> onBackPressed());
        binding.gridview1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(posts.get(position).postId);
                String postId = posts.get(position).postId;
                String postImageUrl = posts.get(position).postImg;
                startActivity(new Intent(PostAccountProfileActivity.this,ProfileAccountPostClicked.class)
                        .putExtra(Constants.POST_IMAGE_URL,postImageUrl)
                        .putExtra(Constants.POST_IMAGE_ID,postId));
            }
        });
        if (preferenceManager.getString(Constants.USER_ID).equals(userId)){
            binding.followButton.setText("Edit Profile");
        }else {
            binding.followButton.setText("Follow");
        }

    }

    private void loadUserInfo(){
        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.USER_ID);
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        binding.profileName.setText(task.getResult().getString(Constants.NAME));
                        //Load image
                        byte[] bytes = Base64.decode(task.getResult().getString(Constants.IMAGE), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.displayName.setText(task.getResult().getString(Constants.NAME));
                    }
                });

        database.collection(Constants.COLLECTION_POST)
                .whereEqualTo(Constants.USER_ID,userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            Integer postLength = task.getResult().size();
                            binding.txtPosts.setText(postLength.toString());
                        }
                    }
                });


    }

    private void setPhotoGridView(){
        loading(true);
        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.USER_ID);
        System.out.println("hello " + userId);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_POST)
                .whereEqualTo(Constants.USER_ID,userId)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if(task.isSuccessful() && task.getResult() != null){
                        for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                            String imageUrl = queryDocumentSnapshot.getString(Constants.POST_IMAGE_URL);
                            images.add(imageUrl);
                            Post post = new Post();
                            post.postId = queryDocumentSnapshot.getId();
                            posts.add(post);
                        }
                        ProfileAccountPostsAdapter profileAccountPostsAdapter = new ProfileAccountPostsAdapter(posts,images,this);
                        binding.gridview1.setAdapter(profileAccountPostsAdapter);

                    }

                });

    }

    private void loading(Boolean loading){
        if(loading){
            //When loading is true
            binding.profileProgressBar.setVisibility(View.VISIBLE);
        }else {
            //When loading is false
            binding.profileProgressBar.setVisibility(View.GONE);
        }
    }
}