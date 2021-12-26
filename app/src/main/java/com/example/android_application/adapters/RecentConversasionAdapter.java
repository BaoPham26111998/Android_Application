package com.example.android_application.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.databinding.ItemContainerRecentConversasionBinding;
import com.example.android_application.listeners.ConversasionListener;
import com.example.android_application.models.ChattingMessage;
import com.example.android_application.models.User;

import java.util.List;

public class RecentConversasionAdapter extends RecyclerView.Adapter<RecentConversasionAdapter.ConversasionViewHolder> {

    public RecentConversasionAdapter(List<ChattingMessage> chattingMessages, ConversasionListener conversasionListener) {
        this.chattingMessages = chattingMessages;
        this.conversasionListener = conversasionListener;
    }

    private final List<ChattingMessage> chattingMessages;
    private ConversasionListener conversasionListener;

    @NonNull
    @Override
    public ConversasionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversasionViewHolder(
                ItemContainerRecentConversasionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversasionViewHolder holder, int position) {
        holder.setData(chattingMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chattingMessages.size();
    }

    class ConversasionViewHolder extends RecyclerView.ViewHolder{
        ItemContainerRecentConversasionBinding binding;

        ConversasionViewHolder(ItemContainerRecentConversasionBinding itemContainerRecentConversasionBinding) {
            super(itemContainerRecentConversasionBinding.getRoot());
            binding = itemContainerRecentConversasionBinding;
        }
        void setData(ChattingMessage chattingMessage){
            binding.imageProfile.setImageBitmap(getConversasionImage(chattingMessage.conversasionImage));
            binding.textName.setText(chattingMessage.conversasionName);
            binding.textRecentMessage.setText(chattingMessage.message);
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.id = chattingMessage.conversasionId;
                user.name = chattingMessage.conversasionName;
                user.image = chattingMessage.conversasionImage;
                conversasionListener.onConversasionClicked(user);
            });
        }
    }
    private Bitmap getConversasionImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
    }
}

