package com.example.beautyapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.beautyapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private LottieAnimationView animationView;
    private TextView splashText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ánh xạ view
        animationView = findViewById(R.id.animation_view);
        splashText = findViewById(R.id.splashtext);

        // Thiết lập animation
        setupAnimation();

        // Delay 3 giây (đủ thời gian xem animation) trước khi kiểm tra đăng nhập
        new Handler().postDelayed(this::checkLoginStatus, 3000);
    }

    private void setupAnimation() {
        // Có thể custom animation tại đây nếu cần
        animationView.setAnimation(R.raw.animationload);
        animationView.playAnimation();
        animationView.loop(true);

        // Hiệu ứng cho text (tuỳ chọn)
        splashText.setText("Beauty App");
        splashText.setAlpha(0f);
        splashText.animate()
                .alpha(1f)
                .setDuration(1500)
                .setStartDelay(500)
                .start();
    }

    private void checkLoginStatus() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {

            navigateToMain();
        } else {
            // Kiểm tra token lưu trữ
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
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, DangNhapActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        // Dọn dẹp animation để tránh memory leak
        if (animationView != null) {
            animationView.cancelAnimation();
        }
        super.onDestroy();
    }
}