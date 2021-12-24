package com.example.android_application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PhotoEditingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);


        
    }

    public void uploadPhoto(){
        String timestamp = "test " + System.currentTimeMillis();
        String firePathAndName = "Images/" + "image" + timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(firePathAndName);
//        storageReference.putFile()
    }
}