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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DangNhapActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText txtEmail,txtPass;
    private Button btnDangNhap,btnDangKy;
    private CheckBox terms_checkbox;

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
                                    FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        if (user != null) {
                                            user.getIdToken(false).addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    String idToken = task1.getResult().getToken();
                                                    // Lưu token vào SharedPreferences
                                                    SharedPreferences pref = getSharedPreferences("auth", MODE_PRIVATE);
                                                    pref.edit().putString("idToken", idToken).apply();
                                                }
                                            });
                                        }
                                    });
                                    Intent intent = new Intent(DangNhapActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
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

    private void anhXa() {
        txtEmail = findViewById(R.id.email);
        txtPass = findViewById(R.id.password);
        btnDangNhap = findViewById(R.id.signup_button);
        terms_checkbox = findViewById(R.id.terms_checkbox);
        btnDangKy = findViewById(R.id.register_button);
    }

}