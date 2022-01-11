package com.example.android_application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.adapters.UserAdapter;
import com.example.android_application.databinding.ActivityAccountPostLikedListBinding;
import com.example.android_application.listeners.ChatUserListener;
import com.example.android_application.models.User;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AccountPostLikedList extends AppCompatActivity implements ChatUserListener {
    ActivityAccountPostLikedListBinding binding;
    PreferenceManager preferenceManager;
    ArrayList<String> userIdLikeList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountPostLikedListBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());


        getUserLiked();
        setListeners();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUserLiked(){
        ArrayList<String> userIdLikeList = (ArrayList<String>) getIntent().getSerializableExtra(Constants.POST_USER_LIKE);
        System.out.println(userIdLikeList);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        List<User> users = new ArrayList<>();
        for (int i = 0; i<userIdLikeList.size(); i++){

            String userID = userIdLikeList.get(i);
            System.out.println(users);
            database.collection(Constants.COLLECTION_USERS)
                    .document(userID)
                    .get()
                    .addOnCompleteListener(task -> {
                        loading(false);
                        if (task.isSuccessful() && task.getResult() != null) {
                            User user = new User();
                            user.name = task.getResult().getString(Constants.NAME);
                            user.email = task.getResult().getString(Constants.EMAIL);
                            user.image = task.getResult().getString(Constants.IMAGE);
                            user.token = task.getResult().getString(Constants.FCM_TOKEN);
                            user.id = task.getResult().getId();
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

                    });
        }

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