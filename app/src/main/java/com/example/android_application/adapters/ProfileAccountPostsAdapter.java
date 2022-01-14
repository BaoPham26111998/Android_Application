package com.example.android_application.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.android_application.R;
import com.example.android_application.models.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileAccountPostsAdapter extends BaseAdapter {
    List<String> imageUrls;
    List<Post> accountProfilePostList ;
    private LayoutInflater layoutInflater;

    public ProfileAccountPostsAdapter(List<Post> accountProfilePostList,
                                      List<String> imageUrls,
                                      Context context) {
        this.imageUrls = imageUrls;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.accountProfilePostList = accountProfilePostList;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = layoutInflater.inflate(R.layout.row_image_account_profile_post, parent,false);
        }
        ImageView postImage = convertView.findViewById(R.id.postImageView);
        Picasso.get().load(imageUrls.get(position)).into(postImage);



        return convertView;
    }
}
