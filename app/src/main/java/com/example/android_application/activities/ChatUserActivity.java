package com.example.android_application.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android_application.adapters.UserAdapter;
import com.example.android_application.databinding.ActivityChatUserBinding;
import com.example.android_application.listeners.ChatUserListener;
import com.example.android_application.models.User;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatUserActivity extends AppCompatActivity implements ChatUserListener {

    private ActivityChatUserBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatUserBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "Users list loading error"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    //Get all users in the database
    private void getUsers(){
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        //Get the user information from the users collection
        database.collection(Constants.COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.USER_ID);
                    if(task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            //Define the current user to avoid display the current user in the user cycle list
                            if(currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constants.NAME);
                            user.email = queryDocumentSnapshot.getString(Constants.EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constants.IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            //Add all the users information in a list except current user
                            users.add(user);
                        }
                        if(users.size() > 0){
                            //Display all the users from the database to the list
                            UserAdapter userAdapter = new UserAdapter(users, this);
                            binding.usersRecycleView.setAdapter(userAdapter);
                            binding.usersRecycleView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    }else {
                        showErrorMessage();
                    }
                });
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
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.USER, user);
        startActivity(intent);
        finish();
    }
}