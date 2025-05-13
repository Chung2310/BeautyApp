package com.example.beautyapp.activity;

import static com.example.beautyapp.utils.Utils.user_current;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import android.Manifest;
import com.example.beautyapp.R;
import com.example.beautyapp.model.ImageModel;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private AppBarLayout appBarLayout;
    private RecyclerView recyclerView;
    private CircleImageView circleImageView;
    private TextView CTuser_name,CTuser_email,CTuser_namsinh;
    private Api api;
    private String mediaPath;
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

        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }


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
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(UserActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080,1080)
                        .start();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            mediaPath = data.getDataString();
            upLoadFile();
        } else {
            Toast.makeText(this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
        }
    }

    private String getPath(Uri uri){
        String result;
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor == null) {
            result = uri.getPath();
        }
        else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(index);
            cursor.close();
        }
        return result;
    }

    private void upLoadFile() {
        String id = String.valueOf(Utils.user_current.getId());
        Uri uri = Uri.parse(mediaPath);  // Lấy đường dẫn từ mediaPath
        File file = new File(getPath(uri));  // Chuyển đổi Uri thành File
        RequestBody requestBody = RequestBody.create(MediaType.parse("avt/*"), file);  // Định dạng file tải lên là image
        MultipartBody.Part fileupload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);  // Chuẩn bị tệp để tải lên

        // Gọi API để tải lên
        Call<ImageModel> call = api.uploadFileAvt(fileupload, id);  // Gửi ID người dùng
        call.enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                ImageModel serverResponse = response.body();
                if (serverResponse != null) {
                    if (serverResponse.isSuccess()) {
                        Utils.user_current.setImage(serverResponse.getResult());
                        Log.d("loiavt",serverResponse.getMessage());
                    } else {
                        Log.d("loiavt",serverResponse.getMessage());
                    }
                } else {
                    Log.d("loiavt",serverResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                Log.d("loiavt", t.getMessage());  // Hiển thị lỗi nếu không thành công
            }
        });
    }


    private void showInfo() {
        CTuser_name.setText(user_current.getName());
        CTuser_email.setText(user_current.getEmail());
        CTuser_namsinh.setText("Năm Sinh: "+user_current.getBirth());
        if(Utils.user_current.getImage().contains("https")){
            Glide.with(getApplicationContext()).load(Utils.user_current.getImage()).placeholder(R.drawable.android).into(circleImageView);
        }
        else {
            String hinh = Utils.BASE_URL+"avt/"+Utils.user_current.getImage();
            Glide.with(getApplicationContext()).load(hinh).placeholder(R.drawable.android).into(circleImageView);
        }
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