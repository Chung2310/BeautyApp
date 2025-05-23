package com.example.beautyapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.beautyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private LottieAnimationView animationView;
    private TextView splashText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        animationView = findViewById(R.id.animation_view);
        splashText = findViewById(R.id.splashtext);

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
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null && currentUser.isEmailVerified()) {
            // Đã đăng nhập và xác minh email → vào trang chính
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Chưa đăng nhập → về trang đăng nhập
            startActivity(new Intent(this, DangNhapActivity.class));
        }

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (animationView != null) {
            animationView.cancelAnimation();
        }
        super.onDestroy();
    }
}
