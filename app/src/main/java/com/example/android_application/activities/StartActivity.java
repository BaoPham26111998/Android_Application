package com.example.android_application.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import androidx.appcompat.app.AppCompatActivity;

import com.example.android_application.databinding.ActivityCreatePostBinding;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;

import com.example.android_application.databinding.ActivityStartBinding;
public class StartActivity  extends AppCompatActivity {

    private ImageView iconImage;
    private LinearLayout linearLayout;
    private Button register;
    private Button login;

    private ActivityStartBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityStartBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());

        binding.linearLayout.animate().alpha(0f).setDuration(20);

        TranslateAnimation animation = new TranslateAnimation(0 , 0 , 0 , -500);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAnimationListener());

        binding.iconImage.setAnimation(animation);

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this , SignUpActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this , SignInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

    }

    private class MyAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

            binding.iconImage.clearAnimation();
            binding.iconImage.setVisibility(View.INVISIBLE);
            binding.linearLayout.animate().alpha(1f).setDuration(1000);

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        if (FirebaseAuth.getInstance().getCurrentUser() != null){
//            startActivity(new Intent(StartActivity.this , MainActivity.class));
//            finish();
//        }
//    }
}
