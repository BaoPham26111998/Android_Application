package com.example.android_application.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.databinding.ItemContainerReceiveMessageBinding;
import com.example.android_application.databinding.ItemContainerSentMessageBinding;
import com.example.android_application.models.ChattingMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final List<ChattingMessage> chattingMessages;
    private final Bitmap receiverProfileImage;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChattingMessage> chattingMessages, Bitmap receiverProfileImage, String senderId) {
        this.chattingMessages = chattingMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else {
            return new ReceiveMessageViewHolder(
                    ItemContainerReceiveMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chattingMessages.get(position));
        } else {
            ((ReceiveMessageViewHolder) holder).setData(chattingMessages.get(position),receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chattingMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chattingMessages.get(position).sendId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChattingMessage chattingMessage){
            binding.textMessage.setText(chattingMessage.message);
            binding.textDateTime.setText(chattingMessage.dateTime);
        }
    }
    static class ReceiveMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceiveMessageBinding binding;

        ReceiveMessageViewHolder(ItemContainerReceiveMessageBinding receiveMessageBinding){
            super(receiveMessageBinding.getRoot());
            binding = receiveMessageBinding;
        }

        void setData(ChattingMessage chattingMessage, Bitmap receiverProfileImage){
            binding.textMessage.setText(chattingMessage.message);
            binding.textDateTime.setText(chattingMessage.dateTime);
            binding.imageProfile.setImageBitmap(receiverProfileImage);
        }
    }
}

