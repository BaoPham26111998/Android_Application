package com.example.android_application.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.R;
import com.example.android_application.models.Post;
import com.example.android_application.models.Story;
import com.mikhaellopez.circularimageview.CircularImageView;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Post[] listData;

    public PostAdapter(Post[] listData){
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.post_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Post storyModel = listData[position];
        holder.name.setText(storyModel.name);
        holder.imageView.setImageResource(storyModel.image);
        holder.date.setText(storyModel.date);
        holder.postImage.setImageResource(storyModel.postImg);
        holder.description.setText(storyModel.description);

    }

    @Override
    public int getItemCount() {
        return listData.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name, description, date;
        ImageView postImage;
        CircularImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.imageView = itemView.findViewById(R.id.profile_image);
            this.name = itemView.findViewById(R.id.profile_name);
            this.date = itemView.findViewById(R.id.profile_date);
            this.description = itemView.findViewById(R.id.post_description);
            this.postImage = itemView.findViewById(R.id.post_image);

        }
    }
}
