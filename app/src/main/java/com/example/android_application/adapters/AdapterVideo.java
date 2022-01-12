package com.example.android_application.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.R;
import com.example.android_application.models.Video;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.HolderView> {

    private Context context;
    private ArrayList<Video> videoArrayList;
    private String username;

    public AdapterVideo(Context context, ArrayList<Video> videoArrayList, String username) {
        this.context = context;
        this.videoArrayList = videoArrayList;
        this.username = username;
    }

    @NonNull
    @Override
    public HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_video, parent, false);
        return new HolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderView holder, int position) {
        // Get Data
        Video video = videoArrayList.get(position);

        String id = video.getId();
        String title = video.getTitle();
        String timestamp = video.getTimestamp();
        String videoUrl = video.getVideoUrl();
        String userId = video.getUser();

        // Format timestamp
        Calendar calender = Calendar.getInstance();
        calender.setTimeInMillis(Long.parseLong(timestamp));
        String formattedDateTime = DateFormat.format("dd/MM/yyyy K:mm a", calender).toString();

        // set Data
        holder.title.setText(title);
        holder.timeTv.setText(formattedDateTime);
        holder.timeTv.setText("Creator " + username);


        setVideoUrl(video, holder);

    }

    public void setVideoUrl(Video video, HolderView holder){
        holder.progressBar.setVisibility(View.VISIBLE);

        String videoUrl = video.getVideoUrl();

        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(holder.videoView);


        Uri videoUri = Uri.parse(videoUrl);
        holder.videoView.setMediaController(mediaController);
        holder.videoView.setVideoURI(videoUri);

        holder.videoView.requestFocus();
        holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });

        holder.videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                // check buffering
                switch(what){
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:{
                        holder.progressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:{
                        holder.progressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:{
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        return true;
                    }
                }
                return false;

            }
        });
        holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    class HolderView extends RecyclerView.ViewHolder{

        VideoView videoView;
        TextView title, timeTv, creatorTv;
        ProgressBar progressBar;

        public HolderView(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            title = itemView.findViewById(R.id.titleTv);;
            timeTv = itemView.findViewById(R.id.timeTv);
            creatorTv = itemView.findViewById(R.id.creatorTv);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
