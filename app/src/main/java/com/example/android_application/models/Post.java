package com.example.android_application.models;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Post implements Serializable {

    public String name;
    public Bitmap imageProfile;
    public String postImg;
    public String date;
    public String title;
    public String description;
    public String like;
    public String comment;
    public String postId;
    public String likeCount;


    public Post() {
    }

    public Post(String name, Bitmap imageProfile, String postImg, String date, String title, String description, String like, String comment, String postId, String likeCount) {
        this.name = name;
        this.imageProfile = imageProfile;
        this.postImg = postImg;
        this.date = date;
        this.title = title;
        this.description = description;
        this.like = like;
        this.comment = comment;
        this.postId = postId;
        this.likeCount = likeCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(Bitmap imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }
}


