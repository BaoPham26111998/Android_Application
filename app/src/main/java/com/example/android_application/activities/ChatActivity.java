package com.example.android_application.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.android_application.adapters.ChatAdapter;
import com.example.android_application.databinding.ActivityChatBinding;
import com.example.android_application.models.ChattingMessage;
import com.example.android_application.models.User;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiverUser;
    private List<ChattingMessage> chattingMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversasionId = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Auto generate activity_chat binding
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadReceiveDetails();
        setListeners();
        init();
        listenMessages();
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chattingMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chattingMessages,
                //Get receiver image
                getBitmapFromEncodedString(receiverUser.image),
                preferenceManager.getString(Constants.USER_ID)
        );
        //Chat recycler for (or infinite scroller) when chatting
        binding.chatRecycleView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void sendMessages(){
        //Upload message including Sender id, receiver id, message, sent time to firestore database
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.SENDER_ID, preferenceManager.getString(Constants.USER_ID));
        message.put(Constants.RECEIVER_ID,receiverUser.id);
        message.put(Constants.MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.TIMESTAMP, new Date());
        database.collection(Constants.COLLECTION_CHAT).add(message);
        //After send the message to the database delete the chat text in the input text blank
        if(conversasionId != null){
            updateConversasion(binding.inputMessage.getText().toString());
        }else {
            HashMap<String, Object> conversasion = new HashMap<>();
            conversasion.put(Constants.SENDER_ID, preferenceManager.getString(Constants.USER_ID));
            conversasion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.NAME));
            conversasion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.IMAGE));
            conversasion.put(Constants.RECEIVER_ID, receiverUser.id);
            conversasion.put(Constants.KEY_RECEIVER_NAME, receiverUser.name);
            conversasion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversasion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversasion.put(Constants.TIMESTAMP, new Date());
            addConversasion(conversasion);

        }
        binding.inputMessage.setText(null);
    }

    private void listenMessages(){
        //Set up listener when sent and receive message
        // Setup message sender listener to preference manager
        database.collection(Constants.COLLECTION_CHAT)
                .whereEqualTo(Constants.SENDER_ID, preferenceManager.getString(Constants.USER_ID))
                .whereEqualTo(Constants.RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);
        // Setup message receiver listener preference manager
        database.collection(Constants.COLLECTION_CHAT)
                .whereEqualTo(Constants.SENDER_ID, receiverUser.id)
                .whereEqualTo(Constants.RECEIVER_ID, preferenceManager.getString(Constants.USER_ID))
                .addSnapshotListener(eventListener);
    }


    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null){
            return;
        }
        if(value != null){
            int count = chattingMessages.size();
            //Create a for loop to react on any change of the Firestore document
            for(DocumentChange documentChange : value.getDocumentChanges()){
                //When recognize the document has changed
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    ChattingMessage chattingMessage = new ChattingMessage();
                    //Save message information such as who send the message, who receive the message, message, date time sent
                    //to Firestore
                    chattingMessage.sendId = documentChange.getDocument().getString(Constants.SENDER_ID);
                    chattingMessage.receiverID = documentChange.getDocument().getString(Constants.RECEIVER_ID);
                    chattingMessage.message = documentChange.getDocument().getString(Constants.MESSAGE);
                    chattingMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.TIMESTAMP));
                    chattingMessage.dateObject = documentChange.getDocument().getDate(Constants.TIMESTAMP);
                    chattingMessages.add(chattingMessage);
                }
            }
            //Sort the message to display which one come first which one come later
            Collections.sort(chattingMessages,(obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if(count == 0){
                chatAdapter.notifyDataSetChanged();
            }else {
                chatAdapter.notifyItemRangeInserted(chattingMessages.size(), chattingMessages.size());
                binding.chatRecycleView.smoothScrollToPosition(chattingMessages.size()-1);
            }
            //Display messages list on the chat screen
            binding.chatRecycleView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        if(conversasionId == null){
            checkForConversasion();
        }
    };


    //Encode image function
    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadReceiveDetails(){
        //Get receive message user info
        receiverUser = (User) getIntent().getSerializableExtra(Constants.USER);
        binding.textName.setText(receiverUser.name);
    }

    private void setListeners(){
        //Go back to previous screen when user click on back icon on the top left corner
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        //Trigger sendMessage() function when user click on icon send message
        binding.layoutSend.setOnClickListener(v -> sendMessages());
    }

    //Set up a readable date time
    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversasion(HashMap<String,Object> conversasion){
        database.collection(Constants.KEY_COLLECTION_COVERSASION)
                .add(conversasion)
                .addOnSuccessListener(documentReference -> conversasionId = documentReference.getId());
    }

    private void updateConversasion(String message){
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_COVERSASION)
                .document(conversasionId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE,message,
                Constants.TIMESTAMP, new Date()
        );
    }

    private void checkForConversasion(){
        if(chattingMessages.size() != 0){
            checkForConversasionRemotely(
                    preferenceManager.getString(Constants.USER_ID),
                    receiverUser.id
            );
            checkForConversasionRemotely(receiverUser.id,
                    preferenceManager.getString(Constants.USER_ID));
        }
    }

    private void checkForConversasionRemotely(String senderId, String receiverId){
        database.collection(Constants.KEY_COLLECTION_COVERSASION)
                .whereEqualTo(Constants.SENDER_ID, senderId)
                .whereEqualTo(Constants.RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversasionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversasionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversasionId = documentSnapshot.getId();
        }
    };
}