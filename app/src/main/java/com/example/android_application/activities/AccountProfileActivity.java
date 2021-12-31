package com.example.android_application.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.databinding.ActivityAccountProfileBinding;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class AccountProfileActivity extends AppCompatActivity {
    private ActivityAccountProfileBinding binding;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountProfileBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        loadUserInfo();
        setListeners();
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
}