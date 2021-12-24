package com.example.android_application.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.databinding.ActivityMainBinding;
import com.example.android_application.models.Video;
import com.example.android_application.adapters.AdapterVideo;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private ArrayList<Video> videoArrayList;
    FirebaseFirestore db;
    private AdapterVideo adapterVideo;
    private RecyclerView recyclerView;
    //Because view binding enabled, binding for each XML file will be generate automatically
    private ActivityMainBinding binding;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding the main activity from the activity_main XML file
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        loadUserInfo();
        getToken();
        setListeners();
    }

    private void setListeners(){
        //log out by click in to the icon on the top right corner
        binding.imageSignOut.setOnClickListener(v -> logOut());
//        binding.fabNewChat.setOnClickListener(v ->
//                startActivity(new Intent(getApplicationContext(), UsersAcitivity.class)));
    }

    // load user info in the application
    private void loadUserInfo() {
        //Load user name
        binding.textName.setText(preferenceManager.getString(Constants.NAME));
        //Load image
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    // Set up the application notification for UI
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        //Ever time user login they will have a different token
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.USER_ID)
                );
        documentReference.update(Constants.FCM_TOKEN, token)
                //.addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e -> showToast("Unable to get Token"));
    }

    //logout function
    private void logOut(){
        showToast("Signing out");
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

    // TODO: Set up the recycler view in wherever you want the video to be displayed before calling
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
                            adapterVideo = new AdapterVideo(MainActivity.this, videoArrayList, preferenceManager.getString(Constants.NAME));
                            recyclerView.setAdapter(adapterVideo);
                        } else{
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}