package com.example.android_application.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.databinding.CommentItemBinding;
import com.example.android_application.models.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    List<Comment> commentList;

    public CommentAdapter(List<Comment> commentList) {
        System.out.println(commentList);
        this.commentList = commentList;
    }


    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemBinding commentItemBinding = CommentItemBinding.inflate(
                LayoutInflater.from(parent.getContext())
        );
        return new CommentViewHolder(commentItemBinding );
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.SetCommentData(commentList.get(position));
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{
        CommentItemBinding binding;

        CommentViewHolder(CommentItemBinding commentItemBinding){
            super(commentItemBinding.getRoot());
            binding = commentItemBinding;
        }

        void SetCommentData(Comment comment){

            byte[] bytes = Base64.decode(comment.imageProfile, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.imageProfile.setImageBitmap(bitmap);
            binding.username.setText(comment.name);
            binding.comment.setText(comment.commentString);

        }

    }
}
