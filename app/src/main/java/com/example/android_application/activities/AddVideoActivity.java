package com.example.android_application.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.android_application.R;
import com.example.android_application.databinding.ActivityAddVideoBinding;
import com.example.android_application.ultilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddVideoActivity extends AppCompatActivity {
    private ActionBar actionBar;
    private ProgressDialog progressDialog;
    private String title;
    EditText editText;
    VideoView videoView;
    Button uploadButton;
    FloatingActionButton pickVidFab;

    FirebaseFirestore db;
    FirebaseAuth mAuth;

//    ActivityAddVideoBinding binding;

    public static int VIDEO_PICK_GALLERY_CODE = 100;
    public static int VIDEO_PICK_CAMERA_CODE = 101;
    public static int CAMERA_REQUEST_CODE = 102;

    private String[] cameraPermission;

    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_video);

        db = FirebaseFirestore.getInstance();
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Add New Video");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Uploading Video...");
        progressDialog.setCanceledOnTouchOutside(false);

        editText = (EditText) findViewById(R.id.editTitle);
        videoView = (VideoView) findViewById(R.id.videoView);
        uploadButton = (Button) findViewById(R.id.uploadButton);
        pickVidFab = (FloatingActionButton) findViewById(R.id.pickVideoTab);

        cameraPermission = new String[]{
            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        mAuth = FirebaseAuth.getInstance();

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = editText.getText().toString().trim();
                if (TextUtils.isEmpty(title)){
                    Toast.makeText(AddVideoActivity.this, "Put The Text In You Cunt", Toast.LENGTH_SHORT).show();
                }
                else if (videoUri==null){
                    Toast.makeText(AddVideoActivity.this, "Put The Video In You Cunt", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadToFirebase();
                }

            }
        });

        pickVidFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoPickDialog();
            }
        });
    }

    private void uploadToFirebase(){
        progressDialog.show();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String uUid = firebaseUser.getUid();

        String timestamp = "" + System.currentTimeMillis();

        String firePathAndName = "Videos/" + "video" + timestamp;

        StorageReference storageRef = FirebaseStorage.getInstance().getReference(firePathAndName);

        storageRef.putFile(videoUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downloadUri = uriTask.getResult();
                        if(uriTask.isSuccessful()){
                            //receive video uploaded url

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put(Constants.VIDEO_ID, "" + timestamp + Double.toString(Math.random()*100));
                            hashMap.put(Constants.VIDEO_TITLE, "" + title);
                            hashMap.put(Constants.VIDEO_TIMESTAMP, "" + timestamp);
                            hashMap.put(Constants.VIDEO_URL, "" + downloadUri);
                            hashMap.put(Constants.VIDEO_CREATOR, "" + uUid);

                            // Add Video Data to firestore
                            db.collection("videos")
                                    .add(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            Toast.makeText(AddVideoActivity.this, "Successfully added to Firestore", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddVideoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    });

//                              Add to realtime db
//                            DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://gacha-17df9-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("Videos");
//                            databaseReference.child(timestamp)
//                                    .setValue(hashMap)
//                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            progressDialog.dismiss();
//                                            Toast.makeText(AddVideoActivity.this, "Video uploaded", Toast.LENGTH_SHORT).show();
//
//                                        }
//                                    })
//                                    .addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            progressDialog.dismiss();
//                                            Toast.makeText(AddVideoActivity.this, "You are a mistake", Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddVideoActivity.this, "Fuck"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void videoPickDialog() {
        String[] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Video From")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            // camera
                            if (!checkCameraPermission()){
                                requestCameraPermission();
                            }
                            else {
                                videoPickCamera();
                            }
                        }
                        else if (i == 1){
                            //gallery
                            videoPickGallery();
                        }
                    }
                }).show();

    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED;

        return result1 && result2;
    }

    private void videoPickGallery(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Videos"), VIDEO_PICK_GALLERY_CODE);
    }

    private void videoPickCamera(){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, VIDEO_PICK_CAMERA_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 102:
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (cameraAccepted && storageAccepted){
                        videoPickCamera();
                    }
                    else {
                        Toast.makeText(this, "Camera & Storage not allowed", Toast.LENGTH_SHORT).show();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setVideoToVideoView() {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.pause();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // called after picking videos
        if (resultCode == RESULT_OK){
            if (requestCode == VIDEO_PICK_GALLERY_CODE){
                videoUri = data.getData();
            } else if ( requestCode == VIDEO_PICK_CAMERA_CODE){
                videoUri = data.getData();
                // show in videoView
                setVideoToVideoView();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}