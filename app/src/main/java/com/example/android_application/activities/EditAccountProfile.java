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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.databinding.ActivityEditAccountProfileBinding;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditAccountProfile extends AppCompatActivity {
    ActivityEditAccountProfileBinding binding;
    PreferenceManager preferenceManager;

    private String encodedImage;
    FirebaseFirestore database = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAccountProfileBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        getAccountInfo();
        setListeners();
    }

    private void setListeners(){
        binding.textReturn.setOnClickListener(v -> onBackPressed());
        binding.buttonConfirm.setOnClickListener(v -> updateProfilePage());
        binding.ChangeProfileImage.setOnClickListener(v -> {
            //Open and Pick image from device media file
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //Allow application to read the media file
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void getAccountInfo(){
        binding.textAddImage.setVisibility(View.INVISIBLE);
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.avatar.setImageBitmap(bitmap);

        database.collection(Constants.COLLECTION_USERS).document(preferenceManager.getString(Constants.USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    binding.inputName.setText(task.getResult().getString(Constants.NAME));
                    //Load imageimage
                    String imageString = task.getResult().getString(Constants.IMAGE);
                    binding.inputDescription.setText(task.getResult().getString(Constants.USER_DESCRIPTION));
                    binding.inputWebsite.setText(task.getResult().getString(Constants.USER_WEBSITE));
                })
                .addOnFailureListener(exception ->{
                    loading(false);
                    showToast(exception.getMessage());
                });


    }

    private void updateProfilePage(){
        if (encodedImage == null){
            encodedImage = preferenceManager.getString(Constants.IMAGE);
        }
        loading(true);
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.NAME,binding.inputName.getText().toString());
        user.put(Constants.USER_DESCRIPTION,binding.inputDescription.getText().toString());
        user.put(Constants.USER_WEBSITE,binding.inputWebsite.getText().toString());
        user.put(Constants.IMAGE, encodedImage);

        database.collection(Constants.COLLECTION_USERS).document(preferenceManager.getString(Constants.USER_ID))
                .update(user)
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });



        database.collection(Constants.COLLECTION_POST)
                .whereEqualTo(Constants.USER_ID,preferenceManager.getString(Constants.USER_ID))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<Object, String> map = new HashMap<>();
                                map.put(Constants.NAME,binding.inputName.getText().toString());
                                map.put(Constants.IMAGE, encodedImage);
                                database.collection(Constants.COLLECTION_POST)
                                        .document(document.getId()).set(map, SetOptions.merge());
                            }
                            Intent intent = new Intent(getApplicationContext(),AccountProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                })
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
                            binding.avatar.setImageBitmap(bitmap);
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
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    //Set up loading animation
    private void loading(Boolean loading){
        if(loading){
            //When loading is true
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            //When loading is false
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}