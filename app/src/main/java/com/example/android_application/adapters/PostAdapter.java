package com.example.android_application.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.activities.CommentActivity;
import com.example.android_application.databinding.PostItemBinding;
import com.example.android_application.models.Post;
import com.example.android_application.ultilities.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.example.android_application.R;

import java.util.ArrayList;
import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

//    private Context mContext;
    private final List<Post> postList;
    private FirebaseUser firebaseUser;
    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }


//    public PostAdapter(Context mContext, List<Post> postList) {
//        this.mContext = mContext;
//        this.postList = postList;
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//    }


    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostItemBinding postItemBinding = PostItemBinding.inflate(
                LayoutInflater.from(parent.getContext())
        );

        return new PostViewHolder(postItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {

        //holder.SetPostData(postList.get(position));
        final Post post = postList.get(position);
        holder.SetPostData(post);


        getComments(post.getPostId(), holder.binding.noOfComments);

        holder.itemView.findViewById(R.id.comment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(v.getContext() , CommentActivity.class);
                    intent.putExtra("postId", post.getPostId());
                    //  intent.putExtra("authorId", );
                    v.getContext().startActivity(intent);
                }
        });


        holder.itemView.findViewById(R.id.no_of_comments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CommentActivity.class);
                intent.putExtra("postId", post.getPostId());
//                intent.putExtra("authorId", post.getPublisher());
                v.getContext().startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{
        PostItemBinding binding;
        private FirebaseAuth mAuth;
        private FirebaseUser firebaseUser;

        PostViewHolder(PostItemBinding postItemBinding){
            super(postItemBinding.getRoot());

            binding = postItemBinding;
        }

        void SetPostData(Post post){
            Picasso.get().load(post.postImg).into(binding.postImage);
            binding.profileImage.setImageBitmap(post.imageProfile);
            binding.postTitle.setText(post.title);
            binding.postDescription.setText(post.description);
            binding.profileName.setText(post.name);
            binding.profileDate.setText(post.date);
            binding.feedsCommentCount.setText(post.comment);
            binding.feedsLikesCount.setText(post.likeCount);
            FirebaseFirestore database = FirebaseFirestore.getInstance();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            firebaseUser = mAuth.getCurrentUser();
            database.collection(Constants.COLLECTION_POST).document(post.postId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            List<String> userList = (List<String>) documentSnapshot.get(Constants.POST_USER_LIKE);
                            Integer likeLength = userList.size();
                            binding.feedsLikesCount.setText(likeLength.toString() + " likes");
                            String userId = mAuth.getUid();
                            if (userList.contains(userId)){
                                System.out.println(true);
                                binding.feedsPostLike.setVisibility(View.GONE);
                                binding.feedsPostLiked.setVisibility(View.VISIBLE);
                            }else {
                                System.out.println(false);
                                binding.feedsPostLike.setVisibility(View.VISIBLE);
                                binding.feedsPostLiked.setVisibility(View.GONE);
                            }

                        }
                    });


            binding.feedsPostLiked.setOnClickListener(v -> {
                System.out.println("unlike");
                unliked(post);
                binding.feedsPostLike.setVisibility(View.VISIBLE);
                binding.feedsPostLiked.setVisibility(View.GONE);
            });
            binding.feedsPostLike.setOnClickListener(v -> {
                liked(post);
                binding.feedsPostLike.setVisibility(View.GONE);
                binding.feedsPostLiked.setVisibility(View.VISIBLE);
            });



        }

        void liked(Post post){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            firebaseUser = mAuth.getCurrentUser();
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            ArrayList list = new ArrayList();
            list.add(mAuth.getUid());
            database.collection(Constants.COLLECTION_POST)
                    .document(post.postId)
                    .update("userLiked", FieldValue.arrayUnion(list.toArray()));
        }

        void unliked(Post post){

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            firebaseUser = mAuth.getCurrentUser();
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            ArrayList list = new ArrayList();
            list.add(mAuth.getUid());
            database.collection(Constants.COLLECTION_POST)
                    .document(post.postId)
                    .update("userLiked",FieldValue.arrayRemove(list.toArray()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            System.out.println("unlike success");
                        }
                    });

        }


    }

    private void getComments (String postId, final TextView text) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.getInstance().collection(Constants.COLLECTION_POST)
                .whereEqualTo(Constants.POST_ID, postId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        text.setText("View All " + value.getDocuments().size() + " Comments");
                    }
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                text.setText("View All " + dataSnapshot.getChildrenCount() + " Comments");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
    });
    }
}
