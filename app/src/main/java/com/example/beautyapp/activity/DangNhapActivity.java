package com.example.beautyapp.activity;

import static com.example.beautyapp.R.id.checkboxTerms;
import static com.example.beautyapp.R.id.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.Lottie;
import com.airbnb.lottie.LottieAnimationView;
import com.example.beautyapp.R;
import com.example.beautyapp.model.User;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText txtEmail,txtPass;
    private Button btnDangNhap,btnDangKy;
    private CheckBox terms_checkbox;
    private CompositeDisposable compositeDisposable;
    private Api api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_nhap);

        anhXa();

        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        if(intent != null){
            txtEmail.setText(intent.getStringExtra("email"));
            txtPass.setText(intent.getStringExtra("pass"));
        }
            btnDangNhap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = txtEmail.getText().toString();
                    String password = txtPass.getText().toString();
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!terms_checkbox.isChecked()){
                        Toast.makeText(getApplicationContext(), "Vui lòng đồng ý với các Điều khoản Dịch vụ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(DangNhapActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        if (user.isEmailVerified()) {
                                            // Lấy token và lưu vào SharedPreferences
                                            user.getIdToken(false).addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    String idToken = task1.getResult().getToken();
                                                    SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
                                                    pref.edit().putString("idToken", idToken).apply();
                                                }
                                            });

                                            getUser(email);
                                            Paper.book().write("user_current", Utils.user_current);
                                            Intent intent = new Intent(DangNhapActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(DangNhapActivity.this,
                                                    "Vui lòng xác minh email trước khi đăng nhập.",
                                                    Toast.LENGTH_LONG).show();
                                            FirebaseAuth.getInstance().signOut();
                                        }
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            });


        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),DangKiActivity.class);
                startActivity(intent);
            }
        });
    }
    private void getUser(String userId){
        compositeDisposable = new CompositeDisposable();
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);

        compositeDisposable.add(api.getUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            Paper.book().write("user_current", userModel.getResult());
                        },throwable -> {

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