package com.example.android_application.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.databinding.ActivityCreatePostBinding;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class CreatePost extends AppCompatActivity {

    private ActivityCreatePostBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
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
        // Redirect from application to devices image media folder to choose avatar when user clickon the avatar frame
        binding.layoutImage.setOnClickListener(v -> {
            //Open and Pick image from device media file
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //Allow application to read the media file
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        binding.buttonCreatePost.setOnClickListener(v -> {
            createPost();
        });
    }

    private void createPost(){
        loading(true);
        ArrayList list = new ArrayList();
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String, Object> postArray = new HashMap<>();
        postArray.put(Constants.POST_TITLE,binding.inputTitle.getText().toString());
        postArray.put(Constants.POST_DESCRIPTION,binding.inputDescription.getText().toString());
        postArray.put(Constants.POST_IMAGE,encodedImage);

        list.add(postArray);
        System.out.println("alo" + list.toString());
        database.collection(Constants.COLLECTION_USERS).document(preferenceManager.getString(Constants.USER_ID))
                //Upload user information to firestore database
                .update("arrayPost", FieldValue.arrayUnion(list.toArray()))
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    showToast("Post created successfully");
                })
                // Throw exception when fail to upload to the firebase
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
        }




    //Convert image from JPEG to Bytes by using bitmap and Android Base 64 encoder library to send to the database
    private String encodeImage(Bitmap bitmap) {
        //Reformat the image size to fit the avatar frame
        int previewWidth = 150;
        int previewHeight  = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //Encode image from JPEG to string Bytes to add to the database
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    //After picked an image from device you will need to receive the result when perform the pick image action
    //The result is the image
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    //When the data was found (or the image has been chosen)
                    if(result.getData() != null){
                        //We will set the URI of the image to grant read permission for the encodeImage function
                        Uri imageUri = result.getData().getData();
                        try{
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            //Call the avatar frame to put the image in.
                            binding.postImage.setImageBitmap(bitmap);
                            //Disable the text Add Image in the avatar frame when there are a image
                            binding.textAddImage.setVisibility(View.GONE);
                            //Then call the encoded image function
                            encodedImage = encodeImage(bitmap);
                            // Throw exception when input image is fail
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
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