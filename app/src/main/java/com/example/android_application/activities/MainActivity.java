package com.example.android_application.activities;

// Duong part
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_application.R;
import com.example.android_application.adapters.AdapterVideo;
import com.example.android_application.databinding.ActivityMainBinding;
import com.example.android_application.models.Video;
import com.example.android_application.ultilities.Constants;
import com.example.android_application.ultilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private ArrayList<Video> videoArrayList;
    FirebaseFirestore db;
    private AdapterVideo adapterVideo;
    private RecyclerView recyclerView;
    private FloatingActionButton videoFab;


    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    //Because view binding enabled, binding for each XML file will be generate automatically

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_main);
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#dedede")));
//
//        getSupportActionBar().setLogo(R.drawable.insta_logo);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());

        videoFab = findViewById(R.id.fabNewVideo);
//        videoFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, AddVideoActivity.class));
//            }
//        });

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_market,
                R.id.navigation_profile
        ).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(binding.navView, navController);
        loadUserInfo();
        getToken();
        setlisteners();
    }

    private void setlisteners(){
        binding.imageSignOut.setOnClickListener(v-> logOut());
        binding.fabNewPost.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),CreatePost.class)));
        binding.imageChat.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainChat.class)));
        binding.imageProfile.setOnClickListener( v-> startActivity(new Intent(getApplicationContext(), AccountProfileActivity.class)));
        binding.imageVideo.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),MainVideo.class)));
    }

    private void loadUserInfo() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_USERS).document(preferenceManager.getString(Constants.USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    binding.textName.setText(task.getResult().getString(Constants.NAME));
                    //Load imageimage
                    String imageString = task.getResult().getString(Constants.IMAGE);
                    byte[] bytes = Base64.decode(task.getResult().getString(Constants.IMAGE), Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    binding.imageProfile.setImageBitmap(bitmap);

                });
    }

    // Set up the application notification for UI
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        //Ever time user login they will have a different token
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.USER_ID)
                );
        documentReference.update(Constants.FCM_TOKEN, token)
                //.addOnSuccessListener(unused -> showToast("Token updated successfully"))
                .addOnFailureListener(e -> showToast("Unable to get Token"));
    }

    //logout function
    private void logOut(){
        new AlertDialog.Builder(this)
                .setTitle("Logout Entry")
                .setMessage("Are you sure you want to logout?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showToast("Signed out");
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        DocumentReference documentReference =
                                database.collection(Constants.COLLECTION_USERS).document(
                                        preferenceManager.getString(Constants.USER_ID)
                                );
                        HashMap<String, Object> updates = new HashMap<>();
                        //delete the token after user logout on the database
                        updates.put(Constants.FCM_TOKEN, FieldValue.delete());
                        documentReference.update(updates)
                                .addOnSuccessListener(unused -> {
                                    preferenceManager.clear();
                                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> showToast("Unable to logout please try again later!"));
                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.ac_favorits:
                Toast.makeText(MainActivity.this, "Favorit is clicked", Toast.LENGTH_SHORT).show();

            case R.id.ac_messanger:
                Toast.makeText(MainActivity.this, "Messanger is clicked", Toast.LENGTH_SHORT).show();

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
