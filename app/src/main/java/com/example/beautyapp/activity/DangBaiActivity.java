package com.example.beautyapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.beautyapp.R;

public class DangBaiActivity extends AppCompatActivity {
    private EditText editTextTitle,editTextContent;
    private ImageView imagePreview;
    private Button btnSelectImage,btnPost;
    private String image="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_bai);

        anhXa();

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void anhXa() {
        editTextContent = findViewById(R.id.editTextContent);
        editTextTitle = findViewById(R.id.editTextTitle);
        imagePreview = findViewById(R.id.imagePreview);
        btnPost = findViewById(R.id.btnPost);
        btnSelectImage = findViewById(R.id.btnSelectImage);

    }
}