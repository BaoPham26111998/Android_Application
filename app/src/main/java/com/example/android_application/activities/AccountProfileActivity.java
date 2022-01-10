package com.example.android_application.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.adapters.ProfileAccountPostsAdapter;
import com.example.android_application.databinding.ActivityAccountProfileBinding;
import com.example.android_application.models.Post;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class AccountProfileActivity extends AppCompatActivity {
    private ActivityAccountProfileBinding binding;
    private PreferenceManager preferenceManager;
    List<String> images = new ArrayList<String>();
    List<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountProfileBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        loadUserInfo();
        setListeners();
        setPhotoGridView();


    }
    private void loadUserInfo() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_POST)
                .whereEqualTo(Constants.USER_ID,preferenceManager.getString(Constants.USER_ID))
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

        database.collection(Constants.COLLECTION_USERS).document(preferenceManager.getString(Constants.USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    binding.profileName.setText(task.getResult().getString(Constants.NAME));
                    //Load imageimage
                    String imageString = task.getResult().getString(Constants.IMAGE);
                    byte[] bytes = Base64.decode(task.getResult().getString(Constants.IMAGE), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.imageProfile.setImageBitmap(bitmap);
                    binding.displayName.setText(task.getResult().getString(Constants.NAME));
                    binding.description.setText(task.getResult().getString(Constants.USER_DESCRIPTION));
                    binding.website.setText(task.getResult().getString(Constants.USER_WEBSITE));
                })
                .addOnFailureListener(exception ->{
                    loading(false);
                    showToast(exception.getMessage());
                });

    }
    private void setListeners(){
        //log out by click in to the icon on the top right corner
        binding.backButton.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainActivity.class)));
        binding.gridview1.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(posts.get(position).postId);
                String postId = posts.get(position).postId;
                String postImageUrl = posts.get(position).postImg;
                startActivity(new Intent(AccountProfileActivity.this,ProfileAccountPostClicked.class)
                        .putExtra(Constants.POST_IMAGE_URL,postImageUrl)
                        .putExtra(Constants.POST_IMAGE_ID,postId));
            }
        });
        binding.editProfile.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),EditAccountProfile.class)));
    }

    private void setPhotoGridView(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_POST)
                .whereEqualTo(Constants.USER_ID,preferenceManager.getString(Constants.USER_ID))
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

    // Set up the application notification for UI
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }


}