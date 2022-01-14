package com.example.android_application.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.databinding.ActivitySignUpBinding;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding the sign up activity from the activity_sign_up XML file
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Call preferenceManager to store the data information as string
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        mAuth = FirebaseAuth.getInstance();
    }

    private void setListeners() {
        // Redirect to previous page when click on sign in button
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        // Trigger the signup function when click on signup button
        binding.buttonSignUp.setOnClickListener(v -> {
            if (invalidSignUpDetail()){
                signUp();
            }
        });
        // Redirect from application to devices image media folder to choose avatar when user clickon the avatar frame
        binding.layoutImage.setOnClickListener(v -> {
            //Open and Pick image from device media file
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //Allow application to read the media file
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    // Set up the application notification for UI
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signUp(){
        loading(true);
        // Call Firebase firestore function
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        mAuth.createUserWithEmailAndPassword(binding.inputEmail.getText().toString(),binding.inputPassword.getText().toString())
                .addOnCompleteListener((task -> {
                    showToast("Account created");
                    if (task.isSuccessful()){
                        loading(false);
                        String userId = mAuth.getCurrentUser().getUid();
                        DocumentReference documentReference = database.collection(Constants.COLLECTION_USERS).document(userId);
                        HashMap<String, Object> user = new HashMap<>();
                        ArrayList userFollowed = new ArrayList();
                        ArrayList followingUser = new ArrayList();
                        user.put("userFollowed", userFollowed);
                        user.put("followingUser",followingUser);
                        user.put(Constants.NAME,binding.inputName.getText().toString());
                        user.put(Constants.EMAIL,binding.inputEmail.getText().toString());
                        user.put(Constants.IMAGE,encodedImage);

                        documentReference.set(user).addOnSuccessListener((OnSuccessListener) (aVoid) -> {
                            preferenceManager.putBoolean(Constants.IS_SIGNED_IN,true);
                            preferenceManager.putString(Constants.NAME,binding.inputName.getText().toString());
                            preferenceManager.putString(Constants.IMAGE,encodedImage);
                            preferenceManager.putString(Constants.USER_ID,documentReference.getId());
                            preferenceManager.putString(Constants.EMAIL,binding.inputEmail.getText().toString());
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                                .addOnFailureListener(exception -> {
                                    loading(false);
                                    showToast(exception.getMessage());
                });
                    }else {
                        showToast("Create email error");
                    }
                }));



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



    // Set up invalid input information
    private Boolean invalidSignUpDetail() {
        //Missing input image
        if (encodedImage == null) {
            showToast("Choose avatar");
            return false;
        //Missing input name
        } else if (binding.inputName.getText().toString().trim().isEmpty()) {
            showToast("Input your name");
            return false;
        //Missing input email
        } else if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Input your email");
            return false;
        //Invalid input email
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Email must contain @abcxyz.com");
            return false;
        //Missing input password
        }else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Input your password");
            return false;
        //Missing input confirm password
        }else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()) {
            showToast("Input your confirm password");
            return false;
        //Password and Confirm password is not same
        }else if (!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())) {
            showToast("Wrong confirm password");
            return false;
        }else {
            return true;
        }
    }

    //Set up loading animation
    private void loading(Boolean loading){
        if(loading){
            //When loading is true
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            //When loading is false
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }
}