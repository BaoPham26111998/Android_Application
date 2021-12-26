package com.example.android_application.models;

public class Upload {

    private String mTitle;
    private String mDescription;
    private String mImageUrl;
    private String mProfileImage;

    public Upload(){

    }

    public Upload(String title,
                  String imageUrl,
                  String description,
                  String profileImage)
                  {
        if(title.trim().equals("")){
            title = "No Name";
        }
        mTitle = title;
        mImageUrl = imageUrl;
        mDescription = description;
        mProfileImage = profileImage;
    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setmImageUrl(String imageUrl){
        mImageUrl = imageUrl;
    }

    public String getTitle(){
        return mTitle;
    }
    public void setTitle(String title){
        mTitle = title;
    }

    public String getDescription(){
        return mDescription;
    }

    public void setDescription(String description){
        mDescription = description;
    }

    public String getProfileImage(){
        return mProfileImage;
    }

    public void setProfileImage(String profileImage){
        mProfileImage = profileImage;
    }

}
