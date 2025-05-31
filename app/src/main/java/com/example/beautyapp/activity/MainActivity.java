package com.example.beautyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.beautyapp.R;
import com.example.beautyapp.databinding.ActivityMainBinding;
import com.example.beautyapp.model.User;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private User user;
    private CircleImageView imageViewAvatar;
    private CompositeDisposable compositeDisposable;
    private Api api;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        Paper.init(getApplicationContext());

        compositeDisposable = new CompositeDisposable();
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);

        firebaseAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Thiết lập AppBarConfiguration
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        // Tìm NavHostFragment và lấy NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            Log.e("mainactivity", "NavHostFragment không tìm thấy với ID nav_host_fragment");
            return; // Thoát nếu không tìm thấy
        }
        NavController navController = navHostFragment.getNavController();

        // Kết nối ActionBar và NavigationView với NavController
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Lấy thông tin user rồi xử lý UI trong callback
        getUser(firebaseAuth.getUid());

        // Xử lý sự kiện chọn item trong NavigationView
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.dangxuat) {
                FirebaseAuth.getInstance().signOut();
                Paper.book().delete("user");
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            Log.e("MainActivity", "NavHostFragment không tìm thấy trong onSupportNavigateUp");
            return super.onSupportNavigateUp();
        }
        NavController navController = navHostFragment.getNavController();
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    void getUser(String userId){
        compositeDisposable.add(api.getUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            user = userModel.getResult();
                            Log.d("anhmain", user.getImage());

                            // Ẩn menu admin nếu role là user hoặc consultant
                            if (user.getRole().equals("user") || user.getRole().equals("consultant")) {
                                binding.navView.getMenu().findItem(R.id.admin).setVisible(false);
                            }
                            anhXa(user);
                            loadImage(user.getImage());

                        },
                        throwable -> {
                            Log.e("MainActivity", "Lỗi lấy user: " + throwable.getMessage());
                        }
                ));
    }

    private void anhXa(User user) {
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        TextView textViewName = headerView.findViewById(R.id.txttenmain);
        TextView textViewEmail = headerView.findViewById(R.id.txtemalmain);
        TextView textViewAge = headerView.findViewById(R.id.txtnamsinhmain);
        imageViewAvatar = headerView.findViewById(R.id.imageView);

        textViewName.setText(user.getName());
        textViewEmail.setText(user.getEmail());
        textViewAge.setText(user.getBirth());

        loadImage(user.getImage());

        imageViewAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), UserActivity.class);
            startActivity(intent);
        });
    }

    void loadImage(String image) {

        String imageUrl;
        if (image != null && image.contains("https")) {
            imageUrl = image;
        } else {
            imageUrl = Utils.BASE_URL + "avt/" + image;
        }

        Glide.with(getApplicationContext())
                .load(imageUrl)
                .placeholder(R.drawable.android)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageViewAvatar);
    }
}
