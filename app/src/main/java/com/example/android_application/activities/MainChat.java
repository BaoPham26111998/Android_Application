package com.example.android_application.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.adapters.RecentConversasionAdapter;
import com.example.android_application.databinding.ActivityMainChatBinding;
import com.example.android_application.listeners.ConversasionListener;
import com.example.android_application.models.ChattingMessage;
import com.example.android_application.models.User;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainChat extends AppCompatActivity implements ConversasionListener {
    private ActivityMainChatBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChattingMessage> conversasion;
    private RecentConversasionAdapter conversasionAdapter;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainChatBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListeners();
        init();
        loadUserInfo();
        getToken();
        setListeners();
        listenConversasion();
    }

    private void init(){
        conversasion = new ArrayList<>();
        conversasionAdapter = new RecentConversasionAdapter(conversasion,this);
        binding.conversasionRecycleView.setAdapter((conversasionAdapter));
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners(){
        //log out by click in to the icon on the top right corner
        binding.imageSignOut.setOnClickListener(v -> onBackPressed());
        binding.fabNewChat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ChatUserActivity.class)));
    }

    // load user info in the application
    private void loadUserInfo() {
        //Load user name
        binding.textName.setText(preferenceManager.getString(Constants.NAME));
        //Load image
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    // Set up the application notification for UI
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversasion(){
        database.collection(Constants.KEY_COLLECTION_COVERSASION)
                .whereEqualTo(Constants.SENDER_ID, preferenceManager.getString(Constants.USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_COVERSASION)
                .whereEqualTo(Constants.RECEIVER_ID, preferenceManager.getString(Constants.USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null){
            return;
        }
        if(value != null){
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderId = documentChange.getDocument().getString(Constants.SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.RECEIVER_ID);
                    ChattingMessage chattingMessage = new ChattingMessage();
                    chattingMessage.sendId = senderId;
                    chattingMessage.receiverID = receiverId;
                    if(preferenceManager.getString(Constants.USER_ID).equals(senderId)){
                        chattingMessage.conversasionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chattingMessage.conversasionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chattingMessage.conversasionId = documentChange.getDocument().getString(Constants.RECEIVER_ID);
                    }else {
                        chattingMessage.conversasionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chattingMessage.conversasionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chattingMessage.conversasionId = documentChange.getDocument().getString(Constants.SENDER_ID);
                    }
                    chattingMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chattingMessage.dateObject = documentChange.getDocument().getDate(Constants.TIMESTAMP);
                    conversasion.add(chattingMessage);
                }else if(documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i< conversasion.size(); i++){
                        String senderId = documentChange.getDocument().getString(Constants.SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.RECEIVER_ID);
                        if(conversasion.get(i).sendId.equals(senderId) && conversasion.get(i).receiverID.equals(receiverId)){
                            conversasion.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversasion.get(i).dateObject = documentChange.getDocument().getDate(Constants.TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversasion,(obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversasionAdapter.notifyDataSetChanged();
            binding.conversasionRecycleView.smoothScrollToPosition(0);
            binding.conversasionRecycleView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        //Ever time user login they will have a different token
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.USER_ID)
                );
        documentReference.update(Constants.FCM_TOKEN, token)
                //.addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e -> showToast("Unable to get Token"));
    }

    //logout function
    private void logOut(){
        showToast("Signing out");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        //delete the token after user logout on the database
        updates.put(Constants.FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable to logout please try again later!"));
    }

    @Override
    public void onConversasionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.USER, user);
        startActivity(intent);
    }
}