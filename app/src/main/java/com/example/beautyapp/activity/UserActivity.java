package com.example.beautyapp.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.beautyapp.R;

import com.example.beautyapp.model.ImageModel;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.ui.user.appointmenthistory.AppointmentHistoryFragment;
import com.example.beautyapp.ui.user.orderhistory.OrderHistoryFragment;
import com.example.beautyapp.ui.user.post.PostFragment;
import com.example.beautyapp.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserActivity extends AppCompatActivity {

    ImageView imgUser;
    TextView tvUserName, tvEmail;
    TabLayout tabLayout;
    ViewPager2 viewPager;
    private String mediaPath, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        initView();
        initData();
        setupViewPager();
        control();
        userId = firebaseAuth.getUid();
    }

    private void initView() {
        imgUser = findViewById(R.id.profile_image_CTuser);
        tvUserName = findViewById(R.id.CTuser_name);
        tvEmail = findViewById(R.id.CTuser_email);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void initData() {
        String userName = Utils.user_current.getName();
        String email = Utils.user_current.getEmail();
        String imageUrl = Utils.user_current.getImage();

        loadImage(imageUrl);
        tvUserName.setText(userName);
        tvEmail.setText(email);
    }

    private void setupViewPager() {
        UserPagerAdapter adapter = new UserPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Bài viết");
                            break;
                        case 1:
                            tab.setText("Lịch sử đặt lịch");
                            break;
                        case 2:
                            tab.setText("Lịch sử mua hàng");
                            break;
                    }
                }).attach();
    }

    private static class UserPagerAdapter extends FragmentStateAdapter {
        public UserPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new PostFragment();
                case 1:
                    return new AppointmentHistoryFragment();
                case 2:
                    return new OrderHistoryFragment();
                default:
                    throw new IllegalArgumentException("Invalid position: " + position);
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    void loadImage(String imageUrl) {
        if (imageUrl != null) {
            String fullImageUrl;
            if (imageUrl.startsWith("http")) {
                fullImageUrl = imageUrl;
            } else {
                fullImageUrl = Utils.BASE_URL + "avt/" + imageUrl;
            }

            Glide.with(UserActivity.this)
                    .load(fullImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.android)
                    .skipMemoryCache(true)
                    .into(imgUser);
        } else {
            imgUser.setImageResource(R.drawable.android);
        }
    }

    private void control() {
        imgUser.setOnClickListener(v -> ImagePicker.with(UserActivity.this)
                .crop()
                .compress(1024)
                .start());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                mediaPath = uri.toString();
                uploadFile(uri);
            } else {
                Toast.makeText(this, "Không lấy được ảnh", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
        }
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

    private void uploadFile(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String id = user.getUid();
        String realPath = getRealPathFromURI(uri);
        if (realPath == null) {
            Toast.makeText(this, "Không thể lấy đường dẫn ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        File file = new File(realPath);
        if (!file.exists()) {
            Toast.makeText(this, "Ảnh không tồn tại", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        Api api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        api.uploadFileAvt(fileUpload, id).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                ImageModel serverResponse = response.body();
                if (serverResponse != null && serverResponse.isSuccess()) {
                    Utils.user_current.setImage(serverResponse.getResult());
                    loadImage(serverResponse.getResult());
                    Toast.makeText(UserActivity.this, "Cập nhật ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                Toast.makeText(UserActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}