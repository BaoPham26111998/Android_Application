package com.example.android_application.adapters;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.R;

import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.HolderView> {
    ArrayList<String> filterNamesList;
    ArrayList<PhotoFilter> filters;
    ArrayList<Drawable> filterImagesList;

    PhotoEditor mPhotoEditor;

    public FilterAdapter(ArrayList<String> filterNamesList, PhotoEditor mPhotoEditor, ArrayList<PhotoFilter> filters) {
        this.filterNamesList = filterNamesList;
//        this.filterImagesList = filterImagesList;
        this.mPhotoEditor = mPhotoEditor;
        this.filters = filters;
    }

    @NonNull
    @Override
    public FilterAdapter.HolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_filters, parent, false);

        return new HolderView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderView holder, int position) {
//        holder.getFilterImage().setImageDrawable(filterImagesList.get(position));
        holder.getTitle().setText(filterNamesList.get(position));
        ImageButton imageButton = holder.getFilterImage();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                mPhotoEditor.setFilterEffect(filters.get(pos));
            }
        });

    }


    @Override
    public int getItemCount() {
        return filterNamesList.size();
    }

    class HolderView extends RecyclerView.ViewHolder{
        ImageButton filterImage;
        TextView title;

        public HolderView(@NonNull View itemView) {
            super(itemView);

            filterImage = itemView.findViewById(R.id.filterView);
            title = itemView.findViewById(R.id.tvFilterName);;
        }

        public ImageButton getFilterImage() {
            return filterImage;
        }

        public TextView getTitle() {
            return title;
        }
    }
}
