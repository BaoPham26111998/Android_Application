package com.example.android_application.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.adapters.ProfileAccountPostsAdapter;
import com.example.android_application.databinding.ActivityAccountProfileBinding;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class AccountProfileActivity extends AppCompatActivity {
    GridView gridView;
    private ActivityAccountProfileBinding binding;
    private PreferenceManager preferenceManager;
    ArrayList<String> images = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountProfileBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        loadUserInfo();
        setListeners();
        gridView = binding.gridview1;
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
        //Load user name
        binding.profileName.setText(preferenceManager.getString(Constants.NAME));
        //Load image
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
        binding.displayName.setText(preferenceManager.getString(Constants.NAME));
    }
    private void setListeners(){
        //log out by click in to the icon on the top right corner
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }

    private void setPhotoGridView(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_POST)
                .whereEqualTo(Constants.USER_ID,preferenceManager.getString(Constants.USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                   if(task.isSuccessful() && task.getResult() != null){
                       loading(false);
                       for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                           String imageUrl = queryDocumentSnapshot.getString(Constants.POST_IMAGE_URL);
                           images.add(imageUrl);
                       }
                       ProfileAccountPostsAdapter profileAccountPostsAdapter = new ProfileAccountPostsAdapter(images,this);
                       gridView.setAdapter(profileAccountPostsAdapter);

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