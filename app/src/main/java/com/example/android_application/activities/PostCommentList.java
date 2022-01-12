package com.example.android_application.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.adapters.CommentAdapter;
import com.example.android_application.databinding.ActivityPostCommentListBinding;
import com.example.android_application.models.Comment;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PostCommentList extends AppCompatActivity  {
    ActivityPostCommentListBinding binding;
    PreferenceManager preferenceManager;
    List<Comment> comments = new ArrayList<>();
    String postId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostCommentListBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        postId = intent.getStringExtra(Constants.POST_IMAGE_ID);
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
                .collection("comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult() != null) {
                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                Comment commentObject = new Comment();
                                commentObject.commentString = documentSnapshot.getString("commentString");
                                commentObject.name = documentSnapshot.getString("name");
                                commentObject.userId = documentSnapshot.getString("userId");
                                commentObject.imageProfile = documentSnapshot.getString("imageProfile");
                                System.out.println(documentSnapshot.getString("name"));
                                System.out.println(documentSnapshot.getString("userId"));
                                System.out.println(documentSnapshot.getString("image"));

                                comments.add(commentObject);
                            }
                            System.out.println(comments.toString());
                        }
                    }

                });
        CommentAdapter commentAdapter = new CommentAdapter(comments);
        binding.commentsRecycleView.setAdapter(commentAdapter);
//        binding.progressBar.setVisibility(View.GONE);

    }


//                            commentArrayList = (ArrayList<Comment>) value.get("comments");

////                            System.out.println(commentArrayList.toString());
//                            for(int i =0; i<=commentArrayList.size();i++){
//                                System.out.println(commentArrayList.get(i).);
//
////                                commentObject.commentString= commentArrayList.get(i).commentString;
////                                commentObject.name = commentArrayList.get(i).name;
////                                commentObject.userId = commentArrayList.get(i).userId;
////                                commentObject.imageProfile = commentArrayList.get(i).imageProfile;
////                                comments.add(commentArrayList.get(i));
}