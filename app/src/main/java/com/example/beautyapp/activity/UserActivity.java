package com.example.beautyapp.activity;

import static com.example.beautyapp.utils.Utils.user_current;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.beautyapp.R;
import com.example.beautyapp.adapter.BaiVietAdapter;
import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.model.ImageModel;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
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
    private TextView CTuser_name, CTuser_email, CTuser_namsinh;
    private Api api;
    private CompositeDisposable compositeDisposable;
    private String mediaPath;
    private List<BaiViet> baiVietList = new ArrayList<>();
    private BaiVietAdapter adapter;
    private boolean isLoading = false;
    private Handler handler = new Handler();
    private LinearLayoutManager linearLayoutManager;
    private int page = 1;
    private String userId = "";

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

        compositeDisposable = new CompositeDisposable();
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        userId = firebaseAuth.getUid();

        anhXa();
        setupAppBarScroll();

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BaiVietAdapter(getApplicationContext(), baiVietList);
        recyclerView.setAdapter(adapter);

        getData(userId, page);
        showInfo();
        control();
        addEvenLoad();
    }

    private void setupAppBarScroll() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                toolbarTitle.setVisibility(View.VISIBLE);
            } else {
                toolbarTitle.setVisibility(View.GONE);
            }
        });
    }

    private void getData(String userId, int page) {
        compositeDisposable.add(api.getAllArticleUser(userId, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        baiVietModel -> {
                            if (baiVietModel.isSuccess()) {
                                if (page == 1) {
                                    baiVietList.clear();
                                }
                                baiVietList.addAll(baiVietModel.getResult());
                                adapter.notifyDataSetChanged();
                            }
                        },
                        throwable -> Log.d("json", "Lỗi khi lấy dữ liệu: " + throwable.getMessage())
                ));
    }

    private void addEvenLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading && linearLayoutManager != null &&
                        linearLayoutManager.findLastCompletelyVisibleItemPosition() == baiVietList.size() - 1) {
                    isLoading = true;
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        handler.post(() -> {
            baiVietList.add(null);
            adapter.notifyItemInserted(baiVietList.size() - 1);
        });

        handler.postDelayed(() -> {
            baiVietList.remove(baiVietList.size() - 1);
            adapter.notifyItemRemoved(baiVietList.size());
            page++;
            getData(userId, page);
            isLoading = false;
        }, 1500);
    }

    private void control() {
        circleImageView.setOnClickListener(v -> ImagePicker.with(UserActivity.this)
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
                upLoadFile(uri);
            } else {
                Toast.makeText(this, "Không lấy được ảnh", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không có hình ảnh nào được chọn", Toast.LENGTH_SHORT).show();
        }
    }

    private void upLoadFile(Uri uri) {
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

        RequestBody requestBody = RequestBody.create(MediaType.parse("avt/*"), file);
        MultipartBody.Part fileupload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        api.uploadFileAvt(fileupload, id).enqueue(new Callback<ImageModel>() {
            @Override
            public void onResponse(Call<ImageModel> call, Response<ImageModel> response) {
                ImageModel serverResponse = response.body();
                if (serverResponse != null && serverResponse.isSuccess()) {
                    user_current.setImage(serverResponse.getResult());
                    Log.d("user",user_current.getImage());
                    showInfo();

                    page = 1;
                    getData(userId, page);
                }

            }

            @Override
            public void onFailure(Call<ImageModel> call, Throwable t) {
                Log.d("user",t.getMessage());
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

    private void showInfo() {

        CTuser_name.setText(user_current.getName());
        CTuser_email.setText(user_current.getEmail());
        CTuser_namsinh.setText("Năm Sinh: " + user_current.getBirth());

        String imageUrl = user_current.getImage();
        if (imageUrl != null) {
            String fullImageUrl;
            if (imageUrl.startsWith("http")) {
                fullImageUrl = imageUrl;
            } else {
                fullImageUrl = Utils.BASE_URL + "avt/" + imageUrl;
            }

            Glide.with(UserActivity.this)
                    .load(fullImageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // Không lưu cache trên disk
                    .skipMemoryCache(true) // Không lưu cache trong bộ nhớ
                    .into(circleImageView);
        } else {
            // Trường hợp imageUrl null thì có thể đặt ảnh mặc định
            circleImageView.setImageResource(R.drawable.android); // thay bằng ảnh mặc định nếu có
        }


    }

    private void anhXa() {
        toolbar = findViewById(R.id.toolBarCTUser);
        setSupportActionBar(toolbar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        appBarLayout = findViewById(R.id.appBarLayout);
        recyclerView = findViewById(R.id.recyclerView);
        CTuser_name = findViewById(R.id.CTuser_name);
        CTuser_email = findViewById(R.id.CTuser_email);
        CTuser_namsinh = findViewById(R.id.CTuser_namsinh);
        circleImageView = findViewById(R.id.profile_image_CTuser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        compositeDisposable.clear();
    }
}
