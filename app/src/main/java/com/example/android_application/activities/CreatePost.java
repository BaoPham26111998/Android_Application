package com.example.android_application.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.databinding.ActivityCreatePostBinding;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreatePost extends AppCompatActivity {

    private ActivityCreatePostBinding binding;
    private PreferenceManager preferenceManager;
    private Uri mImageUri;
    private String imageUrl;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        String userId = preferenceManager.getString(Constants.USER_ID);
        storageReference = FirebaseStorage.getInstance().getReference("posts");
        databaseReference = FirebaseDatabase.getInstance().getReference("post");
        loadUserInfo();
        setListeners();
    }

    private void loadUserInfo() {
        //Load avatar
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
        showToast(preferenceManager.getString(Constants.USER_ID));
    }

    private void setListeners() {
        // Redirect from application to devices image media folder to choose avatar when user click on the avatar frame
        binding.layoutImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                    }
                }

        );
        binding.buttonCreatePost.setOnClickListener(v -> {
            if (storageTask != null && storageTask.isInProgress()){
                showToast("Uploading");
            }else {
                createPost();
            }

        });
        binding.buttonReturn.setOnClickListener(v-> onBackPressed());
        binding.imageReturn.setOnClickListener(v -> onBackPressed());
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && data != null && data.getData() != null){
            mImageUri = data.getData();
            binding.postImage.setImageURI(mImageUri);
            binding.textAddImage.setVisibility(View.GONE);
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void createPost(){
        if(mImageUri != null){
            loading(true);
            StorageReference fileReference = storageReference.child(
                    System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            storageTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            loading(false);

                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                    upLoadToPostCollection();
                                }
                            });

                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            showToast("Upload successful");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loading(false);
                            showToast("Upload fail");
                        }
                    });
        }else {
            showToast("No file selected");
        }
    }

    private void upLoadToPostCollection(){
        FirebaseFirestore database2 = FirebaseFirestore.getInstance();
        HashMap<String, Object> postArray = new HashMap<>();
        postArray.put(Constants.POST_IMAGE_URL,imageUrl);
        postArray.put(Constants.POST_TITLE,binding.inputTitle.getText().toString());
        postArray.put(Constants.POST_DESCRIPTION,binding.inputDescription.getText().toString());
        postArray.put(Constants.USER_ID,preferenceManager.getString(Constants.USER_ID));
        postArray.put(Constants.NAME,preferenceManager.getString(Constants.NAME));
        postArray.put(Constants.IMAGE,preferenceManager.getString(Constants.IMAGE));
        postArray.put(Constants.POST_LIKE,0);
        postArray.put(Constants.POST_COMMENT,0);
        postArray.put(Constants.TIMESTAMP,new Date());
        ArrayList<Object> arrayLike = new ArrayList<>();
        postArray.put("userLiked", arrayLike);
        database2.collection(Constants.COLLECTION_POST).add(postArray)
                .addOnSuccessListener(task -> {
                    showToast("Post added to firebase");
                }).addOnFailureListener(task ->{
            showToast("Post add fail");
        });
    }

    //    private void createPost(){
//        loading(true);
//
//        ArrayList list = new ArrayList();
//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//        HashMap<String, Object> postArray = new HashMap<>();
//        postArray.put(Constants.POST_TITLE,binding.inputTitle.getText().toString());
//        postArray.put(Constants.POST_DESCRIPTION,binding.inputDescription.getText().toString());
//        list.add(postArray);
//
//        upLoadImage();
//        upLoadToPostCollection();
//
//
//        database.collection(Constants.COLLECTION_USERS).document(preferenceManager.getString(Constants.USER_ID))
//                //Upload user information to firestore database
//                .update("arrayPost", FieldValue.arrayUnion(list.toArray()))
//                .addOnSuccessListener(documentReference -> {
//                    loading(false);
//                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    showToast("Post created successfully");
//                })
//                // Throw exception when fail to upload to the firebase
//                .addOnFailureListener(exception -> {
//                    loading(false);
//                    showToast(exception.getMessage());
//                });
//        }


    //After picked an image from device you will need to receive the result when perform the pick image action
    //The result is the image
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    //When the data was found (or the image has been chosen)
                    if(result.getData() != null){
                        //We will set the URI of the image to grant read permission for the encodeImage function
                        mImageUri = result.getData().getData();
                        //Call the avatar frame to put the image in.
                        binding.postImage.setImageURI(mImageUri);
                        //Disable the text Add Image in the avatar frame when there are a image
                        binding.textAddImage.setVisibility(View.GONE);
                        //Then call the encoded image function
                        // Throw exception when input image is fail
                    }else {
                        showToast("Cannot get the Image from the media file");
                    }
                }
            }
    );

    // Set up the application notification for UI
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }


    private void loading(Boolean loading){
        if(loading){
            //When loading is true
            binding.buttonCreatePost.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            //When loading is false
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonCreatePost.setVisibility(View.VISIBLE);
        }
    }
}