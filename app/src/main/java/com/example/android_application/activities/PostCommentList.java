package com.example.android_application.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.android_application.adapters.CommentAdapter;
import com.example.android_application.adapters.PostAdapter;
import com.example.android_application.databinding.ActivityPostCommentListBinding;
import com.example.android_application.models.Comment;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostCommentList extends AppCompatActivity  {
    ActivityPostCommentListBinding binding;
    PreferenceManager preferenceManager;
    List<Comment> commentList = new ArrayList<>();
    CommentAdapter commentAdapter;

    String postId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostCommentListBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        postId = intent.getStringExtra(Constants.POST_IMAGE_ID);


        binding.commentRecycleView.setHasFixedSize(true);
        binding.commentRecycleView.setLayoutManager(new LinearLayoutManager(this));


        binding.post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(binding.addComment.getText().toString())) {
                    Toast.makeText(PostCommentList.this, "No comment added!", Toast.LENGTH_SHORT).show();
                } else {
                    pushComment();
                }
            }
        });


        getCurrentUserInfo();
        setCommentList();

    }

    private void getCurrentUserInfo(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_USERS).document(preferenceManager.getString(Constants.USER_ID))
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        byte[] bytes = Base64.decode(value.getString(Constants.IMAGE), Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        binding.imageProfile.setImageBitmap(bitmap);
                    }
                });
    }


    private void setCommentList() {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_POST)
                .document(postId)
                .collection("comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.w("Listen error",error);
                    return;
                }

                for(DocumentChange documentChange : value.getDocumentChanges()) {
                    //When new comment was add
                    if(documentChange.getType() == DocumentChange.Type.ADDED) {
                        Comment comment = new Comment();
                        comment.name = documentChange.getDocument().getString(Constants.NAME);
                        comment.commentString = documentChange.getDocument().getString("comment");
                        comment.userId = documentChange.getDocument().getString("userId");
                        comment.imageProfile = documentChange.getDocument().getString("image");;
                        commentList.add(comment);
                    }
                    }
                commentAdapter.notifyDataSetChanged();
            }
        });

        commentAdapter = new CommentAdapter(commentList);
        binding.commentRecycleView.setAdapter(commentAdapter);
//                //.addOnSnaListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.getResult() != null) {
//                            List<Comment> commentList = new ArrayList<>();
//                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
//                                Comment commentObject = new Comment();
//                                commentObject.commentString = documentSnapshot.getString("commentString");
//                                commentObject.name = documentSnapshot.getString("name");
//                                commentObject.userId = documentSnapshot.getString("userId");
//                                commentObject.imageProfile = documentSnapshot.getString("image");
//                                System.out.println(documentSnapshot.getString("commentString"));
//                                System.out.println(documentSnapshot.getString("name"));
//                                System.out.println(documentSnapshot.getString("userId"));
//                                System.out.println(documentSnapshot.getString("image"));
//                                commentList.add(commentObject);
//
//                            }
//                            if(commentList.size() > 0){
//                                System.out.println(commentList.size());
//                                CommentAdapter commentAdapter = new CommentAdapter(commentList);
//                                binding.commentRecycleView.setAdapter(commentAdapter);
//                                binding.commentRecycleView.setVisibility(View.VISIBLE);
//                            }
//
//                        }
//
//                    }
//
//                });

    }


    private void pushComment() {
        HashMap<String, Object> map = new HashMap<>();

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        map.put("comment", binding.addComment.getText().toString());
        map.put("userId", preferenceManager.getString(Constants.USER_ID));
        map.put("name", preferenceManager.getString(Constants.NAME));
        map.put("image",preferenceManager.getString(Constants.IMAGE));

        binding.addComment.setText("");

        database.collection(Constants.COLLECTION_POST)
                .document(postId)
                .collection("comments").add(map).addOnSuccessListener(task -> {
            showToast("Comment added to firebase");
        }).addOnFailureListener(task ->{
            showToast("Comment add fail");
        });

    }

    // Set up the application notification for UI
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

}