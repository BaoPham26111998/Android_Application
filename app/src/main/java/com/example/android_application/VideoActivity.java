package com.example.android_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.android_application.activities.AccountProfileActivity;
import com.example.android_application.activities.AddVideoActivity;
import com.example.android_application.activities.CreatePost;
import com.example.android_application.activities.MainActivity;
import com.example.android_application.activities.MainChat;
import com.example.android_application.activities.SignInActivity;
import com.example.android_application.adapters.AdapterVideo;
import com.example.android_application.databinding.ActivityMainBinding;
import com.example.android_application.models.Video;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VideoActivity extends AppCompatActivity {
    private ArrayList<Video> videoArrayList;
    FirebaseFirestore db;
    private AdapterVideo adapterVideo;
    private RecyclerView recyclerView;
    private PreferenceManager preferenceManager;
    private ActivityMainBinding binding;
    private FloatingActionButton newVideoFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.videoRv);
        setListeners();
        loadUserInfo();
        loadVideosFromFirestore();
    }

    private void setListeners(){
        binding.imageSignOut.setOnClickListener(v-> logOut());
        binding.fabNewPost.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), CreatePost.class)));
        binding.imageChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainChat.class)));
        binding.imageProfile.setOnClickListener( v-> startActivity(new Intent(getApplicationContext(), AccountProfileActivity.class)));
        newVideoFab.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), AddVideoActivity.class)));
    }

    private void loadUserInfo() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_USERS).document(preferenceManager.getString(Constants.USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    binding.textName.setText(task.getResult().getString(Constants.NAME));
                    //Load imageimage
                    String imageString = task.getResult().getString(Constants.IMAGE);
                    byte[] bytes = Base64.decode(task.getResult().getString(Constants.IMAGE), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.imageProfile.setImageBitmap(bitmap);
                });
    }

    private void loadVideosFromFirestore(){
        videoArrayList =  new ArrayList<Video>();
        db = FirebaseFirestore.getInstance();

        db.collection("videos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshots : task.getResult()){
                                Video video = new Video();
                                Map<String, Object> videoData = documentSnapshots.getData();
                                video.setId(videoData.get(Constants.VIDEO_ID).toString());
                                video.setVideoUrl(videoData.get(Constants.VIDEO_URL).toString());
                                video.setTitle(videoData.get(Constants.VIDEO_TITLE).toString());
                                video.setTimestamp(videoData.get(Constants.VIDEO_TIMESTAMP).toString());
                                video.setUser(videoData.get(Constants.VIDEO_CREATOR).toString());

                                videoArrayList.add(video);
                            }
                            adapterVideo = new AdapterVideo(VideoActivity.this, videoArrayList, preferenceManager.getString(Constants.NAME));
                            recyclerView.setAdapter(adapterVideo);
                        } else{
                            Toast.makeText(VideoActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void logOut(){
        new AlertDialog.Builder(this)
                .setTitle("Logout Entry")
                .setMessage("Are you sure you want to logout?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showToast("Signed out");
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        DocumentReference documentReference =
                                database.collection(Constants.COLLECTION_USERS).document(
                                        preferenceManager.getString(Constants.USER_ID)
                                );
                        HashMap<String, Object> updates = new HashMap<>();
                        //delete the token after user logout on the database
                        updates.put(Constants.FCM_TOKEN, FieldValue.delete());
                        documentReference.update(updates)
                                .addOnSuccessListener(unused -> {
                                    preferenceManager.clear();
                                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> showToast("Unable to logout please try again later!"));
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }
}