package com.example.android_application.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android_application.R;
import com.example.android_application.adapters.FilterAdapter;
import com.google.android.material.slider.RangeSlider;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;
import ja.burhanrashid52.photoeditor.SaveSettings;
import ja.burhanrashid52.photoeditor.ViewType;

public class PhotoEditingActivity extends AppCompatActivity {
    PhotoEditorView mPhotoEditorView;
    Button brushModeButton, undoButton, filterButton, textButton, exitButton, saveButton, saveTextButton;
    PhotoEditor mPhotoEditor;
    RangeSlider brushSizeRs, brushOpacityRs;
    FilterAdapter filterAdapter;
    RecyclerView rv;
    RelativeLayout brushOptionsLayout;

    ViewGroup _root;
    EditText textEdit;
    private int _xDelta;
    private int _yDelta;

    ArrayList<String> filterNamesList = new ArrayList<>();
    ArrayList<PhotoFilter> filters = new ArrayList<>();
    ArrayList<Drawable> filterImagesList = new ArrayList<>();

    String encodedImage;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Typeface typeface = getResources().getFont(R.font.roboto);

        mPhotoEditorView = findViewById(R.id.photoEditorView);
        brushModeButton = findViewById(R.id.brushMode);
        filterButton = findViewById(R.id.filterButton);
        undoButton = findViewById(R.id.undoButton);
        textButton = findViewById(R.id.textButton);
        exitButton = findViewById(R.id.exitButton);
        saveButton = findViewById(R.id.saveButton);
        saveTextButton = findViewById(R.id.saveButtonText);

        textEdit = findViewById(R.id.textAddition);
        rv = findViewById(R.id.filterRv);

        brushOptionsLayout = findViewById(R.id.brushOption);
        brushSizeRs = findViewById(R.id.brushSizeRs);
        brushOpacityRs = findViewById(R.id.brushOpacityRs);



        // Set image
        Uri imageUri;
        Intent intent = getIntent();
        imageUri = Uri.parse(intent.getStringExtra("imageUri"));
        if (imageUri != null){
            mPhotoEditorView.getSource().setImageURI(imageUri);
        } else {
            mPhotoEditorView.getSource().setImageResource(R.drawable.cat);
        }


        mPhotoEditor = new PhotoEditor.Builder(this, mPhotoEditorView)
                .setPinchTextScalable(true)
                .setDefaultTextTypeface(typeface)
                .setClipSourceImage(true)
                .build();
        setListener();
    }

    void setListener(){
        brushSizeRs.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                int size = (int) value;
                mPhotoEditor.setBrushSize(size);
            }
        });

        brushOpacityRs.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                int opacity = (int) value;
                mPhotoEditor.setOpacity(opacity);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog();
            }
        });

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textEdit.setVisibility(View.VISIBLE);
                textEdit.setHint("Your Text Here...");
                saveTextButton.setVisibility(View.VISIBLE);

                String inputText = textEdit.getText().toString();
//                mPhotoEditor.addText(textEdit.toString(), R.color.text1_color);
                textButton.setEnabled(false);
                filterButton.setEnabled(true);
                brushModeButton.setEnabled(true);
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoEditor.addText(textEdit.getText().toString(), R.color.black);
            }
        });

        brushModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoEditor.setBrushDrawingMode(true);
                brushOptionsLayout.setVisibility(View.VISIBLE);

                brushModeButton.setEnabled(false);
                filterButton.setEnabled(true);
                textButton.setEnabled(true);

                clearFilterList();
                rv.setVisibility(View.GONE);
            }
        });

        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhotoEditor.undo();
                mPhotoEditor.setFilterEffect(PhotoFilter.NONE);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterNamesList.add("Brightness");
                filterNamesList.add("Vignette");
                filterNamesList.add("Fish Eye");
                filterNamesList.add("Negative");
                filterNamesList.add("Gray Scale");
                filters.add(PhotoFilter.BRIGHTNESS);
                filters.add(PhotoFilter.VIGNETTE);
                filters.add(PhotoFilter.FISH_EYE);
                filters.add(PhotoFilter.NEGATIVE);
                filters.add(PhotoFilter.GRAY_SCALE);

                filterAdapter = new FilterAdapter(filterNamesList, mPhotoEditor, filters);
                rv.setAdapter(filterAdapter);
                rv.setVisibility(View.VISIBLE);
                filterButton.setEnabled(false);
                brushOptionsLayout.setVisibility(View.GONE);
                brushModeButton.setEnabled(true);
                textButton.setEnabled(true);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Filters
                clearFilterList();
                rv.setVisibility(View.GONE);
                filterButton.setEnabled(true);

                // Brush
                brushOptionsLayout.setVisibility(View.GONE);
                brushModeButton.setEnabled(true);
                textButton.setEnabled(true);
                mPhotoEditor.setBrushDrawingMode(false);
            }
        });
    }

    public void clearFilterList(){
        filterNamesList.clear();
        filters.clear();
    }

    public void savePhoto(){
            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .build();
            mPhotoEditor.saveAsBitmap(saveSettings, new OnSaveBitmap() {
                @Override
                public void onBitmapReady(Bitmap saveBitmap) {
                    Intent intent = new Intent(PhotoEditingActivity.this, CreatePost.class);
                    intent.putExtra("imageBitmap", encodeImage(saveBitmap));
                    startActivityForResult(intent, 727);
                    Log.e("PhotoEditor","Image Saved Successfully");
                    Toast.makeText(PhotoEditingActivity.this, "Saving Success!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("PhotoEditor","Failed to save Image");
                }
            });
    }

    private String encodeImage(Bitmap bitmap) {
        //Reformat the image size to fit the avatar frame
        int previewWidth = 150;
        int previewHeight  = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //Encode image from JPEG to string Bytes to add to the database
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void alertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Photo Editor");
        builder.setMessage("Are you sure?");
//        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                savePhoto();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                Toast.makeText(PhotoEditingActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}