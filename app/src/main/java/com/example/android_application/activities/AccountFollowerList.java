package com.example.android_application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.adapters.UserAdapter;
import com.example.android_application.databinding.ActivityAccountFollowingListBinding;
import com.example.android_application.listeners.ChatUserListener;
import com.example.android_application.models.User;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AccountFollowerList extends AppCompatActivity implements ChatUserListener {
    ActivityAccountFollowingListBinding binding;
    PreferenceManager preferenceManager;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityAccountFollowingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.USER_ID);
        getUserFollowing();
        setListeners();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUserFollowing(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_USERS)
                .whereArrayContains(Constants.USER_FOLLOWING,userId)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.USER_ID);
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
        Intent intent = new Intent(getApplicationContext(), PostAccountProfileActivity.class);
        intent.putExtra(Constants.USER_ID, user.id);
        startActivity(intent);
        finish();
    }
}