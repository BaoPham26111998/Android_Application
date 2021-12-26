package com.example.android_application.models;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

public class Post implements Serializable {

    public String name;
    public Bitmap imageProfile;
    public Uri postImg;
    public String date;
    public String title;
    public String description;
    public String like;
    public String comment;

    }