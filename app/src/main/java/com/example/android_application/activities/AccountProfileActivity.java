package com.example.android_application.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.databinding.ActivityAccountProfileBinding;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;


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
        //Load user name
        binding.textName.setText(preferenceManager.getString(Constants.NAME));
        //Load image
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
    private void setListeners(){
        //log out by click in to the icon on the top right corner
        binding.backButton.setOnClickListener(v -> onBackPressed());
    }
}