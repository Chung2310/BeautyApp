package com.example.beautyapp.activity;

import static com.example.beautyapp.utils.Utils.user_current;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.google.android.material.appbar.AppBarLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private AppBarLayout appBarLayout;
    private RecyclerView recyclerView;
    private CircleImageView circleImageView;
    private TextView CTuser_name,CTuser_email,CTuser_namsinh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        anhXa();
        showInfo();
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                // Khi AppBarLayout thu gọn hoàn toàn (RecyclerView chiếm toàn màn hình)
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    toolbarTitle.setVisibility(View.VISIBLE); // Hiển thị tiêu đề
                } else {
                    toolbarTitle.setVisibility(View.GONE); // Ẩn tiêu đề
                }
            }
        });
    }

    private void showInfo() {
        CTuser_name.setText(user_current.getName());
        CTuser_email.setText(user_current.getEmail());
        CTuser_namsinh.setText("Năm Sinh: "+user_current.getAge());
        Glide.with(this)
                .load(user_current.getImage())
                .placeholder(R.drawable.android)
                .into(circleImageView);
    }

    private void anhXa() {
        toolbar = findViewById(R.id.toolBarCTUser);
        setSupportActionBar(toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        appBarLayout = findViewById(R.id.appBarLayout);
        recyclerView = findViewById(R.id.recyclerView);
        CTuser_name=findViewById(R.id.CTuser_name);
        CTuser_email=findViewById(R.id.CTuser_email);
        CTuser_namsinh=findViewById(R.id.CTuser_namsinh);
        circleImageView = findViewById(R.id.profile_image_CTuser);
    }
}