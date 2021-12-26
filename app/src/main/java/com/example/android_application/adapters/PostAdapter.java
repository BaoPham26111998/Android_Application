package com.example.android_application.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.databinding.PostItemBinding;
import com.example.android_application.models.Post;

import java.util.List;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private final List<Post> postList;
    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PostItemBinding postItemBinding = PostItemBinding.inflate(
                LayoutInflater.from(parent.getContext())
        );
        return new PostViewHolder(postItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {
        holder.SetPostData(postList.get(position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{
        PostItemBinding binding;
        PostViewHolder(PostItemBinding postItemBinding){
            super(postItemBinding.getRoot());
            binding = postItemBinding;
        }
        void SetPostData(Post post){
            binding.postImage.setImageURI(post.postImg);
            binding.profileImage.setImageBitmap(post.imageProfile);
            binding.postTitle.setText(post.title);
            binding.postDescription.setText(post.description);
            binding.profileName.setText(post.name);
            binding.profileDate.setText(post.date);
        }
    }
}
