package com.example.beautyapp.activity;

import static com.example.beautyapp.utils.Utils.user_current;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private AppBarLayout appBarLayout;
    private RecyclerView recyclerView;
    private CircleImageView circleImageView;
    private TextView CTuser_name,CTuser_email,CTuser_namsinh;
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference mStorageRef;
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

        mStorageRef = FirebaseStorage.getInstance().getReference();

        anhXa();
        showInfo();
        control();
        
        
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

    private void control() {
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Upload ảnh lên Firebase Storage
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            // Tạo reference tới file trên Firebase Storage
            // Ở đây sử dụng timestamp làm tên file để tránh trùng lặp
            StorageReference fileReference = mStorageRef.child("images/"
                    + System.currentTimeMillis() + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Lấy URL download sau khi upload thành công
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    Toast.makeText(getApplicationContext(), "Upload thành công: " + downloadUrl, Toast.LENGTH_LONG).show();

                                    // Bạn có thể lưu downloadUrl vào Realtime Database nếu cần
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Upload thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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