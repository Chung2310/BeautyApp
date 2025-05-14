package com.example.beautyapp.activity;

import static com.example.beautyapp.utils.Utils.user_current;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.example.beautyapp.model.ImageModel;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DangBaiActivity extends AppCompatActivity {
    private EditText editTextTitle, editTextContent;
    private ImageView imagePreview;
    private Button btnSelectImage, btnPost;
    private String mediaPath;
    private CompositeDisposable compositeDisposable;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_bai);

        compositeDisposable = new CompositeDisposable();
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        anhXa();

        btnSelectImage.setOnClickListener(v -> ImagePicker.with(DangBaiActivity.this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start());

        btnPost.setOnClickListener(v -> {
            String content = editTextContent.getText().toString().trim();
            String date = "";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                date = LocalDate.now().toString() + " " + LocalTime.now().toString();
            }

            if (mediaPath == null || mediaPath.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ảnh trước khi đăng bài", Toast.LENGTH_SHORT).show();
                return;
            }

            uploadImageThenPost(date, content);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            mediaPath = data.getDataString();

            Glide.with(this)
                    .load(mediaPath)
                    .placeholder(R.drawable.android)
                    .into(imagePreview);
        } else {
            Toast.makeText(this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageThenPost(String date, String content) {
        String id = String.valueOf(user_current.getUser_id());
        Uri uri = Uri.parse(mediaPath);
        File file = new File(getRealPathFromURI(uri));

        if (!file.exists()) {
            Toast.makeText(this, "Lỗi khi xử lý hình ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("avt/*"), file);
        MultipartBody.Part fileupload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        api.uploadFileImage(fileupload, id).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                ImageModel serverResponse = response.body();
                if (serverResponse != null && serverResponse.isSuccess()) {
                    String filename = serverResponse.getResult();
                    user_current.setImage(filename);

                    compositeDisposable.add(api.addArticle(
                                    user_current.getUser_id(),
                                    date,
                                    content,
                                    0,
                                    filename
                            )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    messageModel -> {
                                        if (messageModel.isSuccess()) {
                                            Toast.makeText(getApplicationContext(), "Đăng bài thành công!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Đăng bài thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    },
                                    throwable -> {
                                        Log.d("dangbai", throwable.getMessage());
                                        Toast.makeText(getApplicationContext(), "Lỗi: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            ));
                } else {
                    Toast.makeText(DangBaiActivity.this, "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                Log.d("uploadAvt", t.getMessage());
                Toast.makeText(DangBaiActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return contentUri.getPath();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private void anhXa() {
        editTextContent = findViewById(R.id.editTextContent);
        editTextTitle = findViewById(R.id.editTextTitle);
        imagePreview = findViewById(R.id.imagePreview);
        btnPost = findViewById(R.id.btnPost);
        btnSelectImage = findViewById(R.id.btnSelectImage);
    }
}
