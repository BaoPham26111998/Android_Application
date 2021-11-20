package com.example.android_application.models;

import java.io.Serializable;

public class Post implements Serializable {

    public String postImage, postTitle, postDescription;

    public Post(){

    }

    public Post(String postImage, String postTitle, String postDescription) {
        this.postImage = postImage;
        this.postTitle = postTitle;
        this.postDescription = postDescription;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }


}