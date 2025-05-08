package com.example.beautyapp.activity;

import static com.example.beautyapp.utils.Utils.user_current;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.example.beautyapp.model.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beautyapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.dangxuat) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, DangNhapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                binding.drawerLayout.closeDrawers();
                return true;
            }
            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                binding.drawerLayout.closeDrawers();
            }
            return handled;
        });

        showInfo();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void showInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Lấy dữ liệu từ Firebase Authentication
            String name = user.getDisplayName() != null ? user.getDisplayName() : "Người dùng";
            String email = user.getEmail() != null ? user.getEmail() : "Không có email";

            // Cập nhật UI ngay với dữ liệu từ Firebase Authentication
            NavigationView navigationView = binding.navView;
            View headerView = navigationView.getHeaderView(0);
            TextView textViewName = headerView.findViewById(R.id.txttenmain);
            TextView textViewEmail = headerView.findViewById(R.id.txtemalmain);
            TextView textViewAge = headerView.findViewById(R.id.txtnamsinhmain);
            ImageView imageViewAvatar = headerView.findViewById(R.id.imageView);

            imageViewAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                    startActivity(intent);
                }
            });

            textViewName.setText(name);
            textViewEmail.setText(email);

            // Lấy dữ liệu bổ sung từ Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String fullName = documentSnapshot.getString("name");
                            String age = documentSnapshot.getString("age");
                            Uri photoUrl = Uri.parse(documentSnapshot.getString("image"));
                            if (fullName != null) {
                                textViewName.setText(fullName);
                                textViewAge.setText("Năm sinh: "+age);
                                if (photoUrl != null) {
                                    Glide.with(this)
                                            .load(photoUrl)
                                            .placeholder(R.drawable.android)
                                            .into(imageViewAvatar);
                                }
                                User user1 = new User();
                                user1.setUser_id(user.getUid());
                                user1.setAge(age);
                                user1.setImage(photoUrl);
                                user1.setEmail(email);
                                user1.setName(fullName);
                                user_current=user1;
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this, "Lỗi đọc dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }



}