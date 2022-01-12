package com.example.android_application.models;

public class Comment {

    private String id;
    private String comment;
    private String publisher;
    private String postId;

    public Comment() {
    }

    public Comment(String id, String comment, String publisher, String postId) {
        this.id = id;
        this.comment = comment;
        this.publisher = publisher;
        this.postId = postId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
