package com.example.android_application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.databinding.ActivitySigninBinding;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignInActivity extends AppCompatActivity {

    private ActivitySigninBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding the sign in activity from the activity_sign_in XML file
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListeners();

    }


    private void setListeners(){
        //Go to sign up screen when user click on the sign button
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        //Trigger sign in function when isValidSignInDetails() function return true
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }


    //Set up loading animation
    private void loading(Boolean isloading){
        if(isloading){
            //When loading is true
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            //When loading is false
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }


    private void signIn(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(binding.inputEmail.getText().toString(),binding.inputPassword.getText().toString())
                .addOnCompleteListener(task -> {

                    firebaseUser = mAuth.getCurrentUser();
                    database.collection(Constants.COLLECTION_USERS).document(mAuth.getUid())
                            .get()
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful() && task1.getResult() != null){
                                    loading(false);
                                    DocumentSnapshot documentSnapshot = task1.getResult();
                                    preferenceManager.putBoolean(Constants.IS_SIGNED_IN, true);
                                    preferenceManager.putString(Constants.USER_ID, documentSnapshot.getId());
                                    preferenceManager.putString(Constants.NAME, documentSnapshot.getString(Constants.NAME));
                                    preferenceManager.putString(Constants.IMAGE, documentSnapshot.getString(Constants.IMAGE));
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    loading(false);
                                    showToast("Unable to sign in");
                                }
                            });
                });
    }




    // Set up the application notification for UI
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    //Check if user has input all the correct sign-in information
    private Boolean isValidSignInDetails(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Please enter your email!");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Invalid email format, email must contain @xyz.com");
            return false;
        }else if (binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Please enter your password!");
            return false;
        }else {
            return true;
        }
    }
}