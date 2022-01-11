package com.example.android_application.listeners;

import com.example.android_application.models.Post;

public interface PostListener {
    void onImageProfileClicked(Post post);
    void onLikedCountClicked(Post post);
}
