package com.example.beautyapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.beautyapp.R;
import com.example.beautyapp.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.HttpURLConnection;
import java.net.URL;

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

        new Handler().postDelayed(() -> {
            if (!isConnected()) {
                showConnectionError("Không có kết nối Internet");
                return;
            }

            auth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser == null || !currentUser.isEmailVerified()) {
                // Chưa đăng nhập hoặc chưa xác minh email → chuyển sang đăng nhập
                startActivity(new Intent(this, DangNhapActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                // Đã đăng nhập → kiểm tra server riêng
                checkServerConnection(() -> {
                    // Server OK → vào MainActivity
                    startActivity(new Intent(this, MainActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }, () -> {
                    // Server lỗi → hiện lỗi
                    showConnectionError("Không kết nối được tới máy chủ dữ liệu");
                });
            }
        }, 3000);
    }

    private void setupAnimation() {
        animationView.setAnimation(R.raw.animationload); // animation khởi động
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

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showConnectionError(String message) {
        animationView.setAnimation(R.raw.erroranimation); // đổi thành animation lỗi của bạn
        animationView.playAnimation();
        animationView.loop(false);

        splashText.setText(message);
        splashText.animate()
                .alpha(1f)
                .setDuration(1500)
                .start();

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void checkServerConnection(Runnable onSuccess, Runnable onFail) {
        new Thread(() -> {
            try {
                // Gọi API đơn giản để kiểm tra server (thay URL bằng của bạn)
                URL url = new URL(Utils.BASE_URL+"connect.php"); // 🔁 Đổi thành API kiểm tra server của bạn
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000); // 3 giây timeout
                connection.connect();

                if (connection.getResponseCode() == 200) {
                    runOnUiThread(onSuccess);
                } else {
                    runOnUiThread(onFail);
                }

            } catch (Exception e) {
                runOnUiThread(onFail);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (animationView != null) {
            animationView.cancelAnimation();
        }
        super.onDestroy();
    }
}
