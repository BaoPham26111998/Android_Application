package com.example.android_application.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.models.Post;
import com.example.android_application.ultilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.android_application.activities.MainActivity;
import com.example.android_application.models.Comment;
import com.example.android_application.models.User;
import com.example.android_application.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private Context mContext;
    private List<Comment> mComments;

    String postId;

    private FirebaseUser fUser;

    public CommentAdapter(Context mContext, List<Comment> mComments , String postId) {
        this.mContext = mContext;
        this.mComments = mComments;
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        final Comment comment = mComments.get(position);

        holder.commenTxt.setText(comment.getComment());
        holder.SetCommentData(comment);

//        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//
//                holder.username.setText(user.getName());
//                if (user.getImage().equals("default")) {
//                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
//                } else {
//                    Picasso.get().load(user.getImage()).into(holder.imageProfile);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        FirebaseFirestore database = FirebaseFirestore.getInstance();
//
//        database.getInstance().collection(Constants.COLLECTION_USERS)
//                .whereEqualTo(Constants.USER_ID, comment.getPublisher()).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//
//
////                holder.username.setText(user.getName());
////                if (user.getImage().equals("default")) {
////                    holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
////                } else {
////                    Picasso.get().load(user.getImage()).into(holder.imageProfile);
////                }
//            }
//        });

        holder.commenTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("publisherId", comment.getPublisher());
                mContext.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (comment.getPublisher().endsWith(fUser.getUid())) {
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            FirebaseDatabase.getInstance().getReference().child("Comments")
                                    .child(postId).child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(mContext, "Comment deleted successfully!", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                    });

                    alertDialog.show();
                }

                return true;
            };
        });

    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public TextView username;
        public TextView commenTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            System.out.println("Get View Holder");
            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            commenTxt = itemView.findViewById(R.id.comment);
        }

        void SetCommentData(Comment comment) {
//            comment post id
//            user id
//            string commenti

//            System.out.println(comment.getPublisher());
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.COLLECTION_POST)
                    .document(postId)
                    .get()
                    .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult()!= null){
                                ArrayList<Object> commentList = new ArrayList<>();
                                HashMap<String,String> commentValue = new HashMap<>();
//                                Map<String, Object> myMap = (Map<String, Object>) task.getResult().get("comments");
                                commentList = (ArrayList<Object>) task.getResult().get("comments");
//                                System.out.println(commentList.size());
                                for (int i =0; i< commentList.size();i++){
                                    Map<ArrayList, Object> myMap = (Map<ArrayList, Object>) commentList.get(i);
                                    System.out.println(myMap.values());
//                                    commentValue.put(commentList[i].)
//                                    comment.setComment(commentList.get(i).);
//                                    System.out.println(commentList.get(i));
                                }

                            }
                    });



        }
    }

}
