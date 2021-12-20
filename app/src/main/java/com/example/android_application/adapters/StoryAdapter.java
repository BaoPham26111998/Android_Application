package com.example.android_application.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.R;
import com.example.android_application.models.Story;
import com.mikhaellopez.circularimageview.CircularImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder> {

    private Story[] listData;

    public StoryAdapter(Story[] listData){
        this.listData = listData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.story_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final  Story Story = listData[position];
        holder.name.setText(Story.name);
        holder.imageView.setImageResource(Story.image);

    }

    @Override
    public int getItemCount() {
        return listData.length;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CircularImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.imageView = itemView.findViewById(R.id.story_image);
            this.name = itemView.findViewById(R.id.story_name);
        }
    }

}
