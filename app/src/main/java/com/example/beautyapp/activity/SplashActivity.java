package com.example.beautyapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.beautyapp.R;
import com.example.beautyapp.model.User;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    private LottieAnimationView animationView;
    private TextView splashText;
    private CompositeDisposable compositeDisposable;
    private Api api;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        animationView = findViewById(R.id.animation_view);
        splashText = findViewById(R.id.splashtext);
        compositeDisposable = new CompositeDisposable();
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);

        Paper.init(this);
        setupAnimation();

        new Handler().postDelayed(this::checkLoginStatus, 3000);
    }

    private void setupAnimation() {
        animationView.setAnimation(R.raw.animationload);
        animationView.playAnimation();
        animationView.loop(true);

        splashText.setText("Beauty App");
        splashText.setAlpha(0f);
        splashText.animate()
                .alpha(1f)
                .setDuration(1500)
                .setStartDelay(500)
                .start();
    }

    private void checkLoginStatus() {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            navigateToMain();
        } else {
            checkSavedToken();
        }
    }

    private void checkSavedToken() {
        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
        String savedToken = pref.getString("idToken", null);

        if (savedToken != null) {
            FirebaseAuth.getInstance().signInWithCustomToken(savedToken)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            navigateToMain();
                        } else {
                            navigateToLogin();
                        }
                    });
        } else {
            navigateToLogin();
        }
    }

    private void navigateToMain() {
        getUserAndNavigate(); // Đợi lấy user xong rồi mới chuyển màn hình
    }

    private void getUserAndNavigate() {
        if (currentUser == null) {
            navigateToLogin();
            return;
        }

        String userId = currentUser.getUid();
        compositeDisposable.add(api.getUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            User user = userModel.getResult();
                            Paper.book().write("user_current", user);
                            Utils.user_current = user;

                            startActivity(new Intent(this, MainActivity.class));
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        },
                        throwable -> {
                            Log.e("Splash", "Lỗi khi lấy user: " + throwable.getMessage());
                            navigateToLogin();
                        }
                ));
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, DangNhapActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (animationView != null) {
            animationView.cancelAnimation();
        }
        compositeDisposable.clear();
        super.onDestroy();
    }
}
