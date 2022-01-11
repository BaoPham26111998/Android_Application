package com.example.android_application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.adapters.UserAdapter;
import com.example.android_application.databinding.ActivityAccountFollowerListBinding;
import com.example.android_application.listeners.ChatUserListener;
import com.example.android_application.models.User;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AccountFollowingList extends AppCompatActivity implements ChatUserListener {
    ActivityAccountFollowerListBinding binding;
    PreferenceManager preferenceManager;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountFollowerListBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.USER_ID);
        getUserFollower();
        setListeners();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
    private void getUserFollower(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_USERS)
                .whereArrayContains(Constants.USER_FOLLOWERS,userId)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<User> users = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                User user = new User();
                                user.name = queryDocumentSnapshot.getString(Constants.NAME);
                                user.email = queryDocumentSnapshot.getString(Constants.EMAIL);
                                user.image = queryDocumentSnapshot.getString(Constants.IMAGE);
                                user.token = queryDocumentSnapshot.getString(Constants.FCM_TOKEN);
                                user.id = queryDocumentSnapshot.getId();
                                //Add all the users information in a list except current user
                                users.add(user);
                            }
                            if (users.size() > 0) {
                                //Display all the users from the database to the list
                                UserAdapter userAdapter = new UserAdapter(users, this);
                                binding.usersRecycleView.setAdapter(userAdapter);
                                binding.usersRecycleView.setVisibility(View.VISIBLE);
                            } else {
                                showErrorMessage();
                            }
                        } else {
                            showErrorMessage();
                        }
                });
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "Users list loading error"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
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


    @Override
    public void onUserClicked(User user) {
        if(user.id.equals(preferenceManager.getString(Constants.USER_ID))){
            Intent intent = new Intent(getApplicationContext(), AccountProfileActivity.class);
            intent.putExtra(Constants.USER_ID, user.id);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), PostAccountProfileActivity.class);
            intent.putExtra(Constants.USER_ID, user.id);
            startActivity(intent);
            finish();
        }

    }
}