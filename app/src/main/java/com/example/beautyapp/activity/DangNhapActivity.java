package com.example.beautyapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

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

public class DangNhapActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText txtEmail, txtPass;
    private Button btnDangNhap, btnDangKy;
    private CheckBox terms_checkbox;
    private CompositeDisposable compositeDisposable;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_dang_nhap);

        anhXa();

        firebaseAuth = FirebaseAuth.getInstance();
        Paper.init(this);
        compositeDisposable = new CompositeDisposable();
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);

        Intent intent = getIntent();
        if (intent != null) {
            txtEmail.setText(intent.getStringExtra("email"));
            txtPass.setText(intent.getStringExtra("pass"));
        }

        btnDangNhap.setOnClickListener(v -> {
            String email = txtEmail.getText().toString();
            String password = txtPass.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!terms_checkbox.isChecked()) {
                Toast.makeText(getApplicationContext(), "Vui lòng đồng ý với các Điều khoản Dịch vụ", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(DangNhapActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                user.getIdToken(false).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        String idToken = task1.getResult().getToken();
                                        SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
                                        pref.edit().putString("idToken", idToken).apply();
                                    }
                                });

                                fetchUserInfo(user.getUid());

                                Intent main = new Intent(DangNhapActivity.this, MainActivity.class);
                                startActivity(main);
                                finish();
                            } else {
                                Toast.makeText(DangNhapActivity.this,
                                        "Vui lòng xác minh email trước khi đăng nhập.",
                                        Toast.LENGTH_LONG).show();
                                FirebaseAuth.getInstance().signOut();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnDangKy.setOnClickListener(v -> {
            Intent intent1 = new Intent(getApplicationContext(), DangKiActivity.class);
            startActivity(intent1);
        });
    }

    private void fetchUserInfo(String userId) {
        compositeDisposable.add(api.getUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            User user = userModel.getResult();
                            if (user != null) {
                                Paper.book().write("user_current", user);
                                Paper.book().write("userId", user.getUser_id());
                            }
                        },
                        throwable -> {
                            // Xử lý lỗi nếu cần
                        }
                ));
    }

    private void anhXa() {
        txtEmail = findViewById(R.id.email);
        txtPass = findViewById(R.id.password);
        btnDangNhap = findViewById(R.id.signup_button);
        terms_checkbox = findViewById(R.id.terms_checkbox);
        btnDangKy = findViewById(R.id.register_button);
    }
}
