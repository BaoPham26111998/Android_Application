package com.example.android_application.models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

public class Post implements Serializable {

    public String name;
    public Bitmap imageProfile;
    public String postVideo;
    public String postImg;
    public String date;
    public String title;
    public String description;
    public String comment;
    public String postId;
    public String likeCount;
    public String userId;
    public ArrayList<String> userIdList;
    public int userCommentList;
}