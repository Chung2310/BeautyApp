package com.example.beautyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.beautyapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DangKiActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextInputEditText txtemailDK, txtpassDK, txtpassxacnhan, txtten, txtnamsinh;
    private AppCompatButton btnDangKy;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ki);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();
        anhXa();

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtemailDK.getText().toString().trim();
                String password = txtpassDK.getText().toString().trim();
                String passwordXacnhan = txtpassxacnhan.getText().toString().trim();
                String hoten = txtten.getText().toString().trim();
                String namsinh = txtnamsinh.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || passwordXacnhan.isEmpty() || hoten.isEmpty() || namsinh.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!password.equals(passwordXacnhan)) {
                    Toast.makeText(getApplicationContext(), "Mật khẩu không trùng khớp", Toast.LENGTH_LONG).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(DangKiActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    Toast.makeText(getApplicationContext(), "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    String uid = user.getUid(); // Lấy UID người dùng

                                    // Tạo dữ liệu người dùng lưu vào Firestore
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("name", hoten);
                                    userData.put("email", email);
                                    userData.put("age", namsinh);
                                    userData.put("image", "");

                                    db.collection("users")
                                            .document(uid) // Dùng UID làm ID document
                                            .set(userData)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("FIRESTORE", "User data saved");
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.w("FIRESTORE", "Error saving user data", e);
                                            });
                                    Intent intent = new Intent(DangKiActivity.this, DangNhapActivity.class);
                                    intent.putExtra("email", email);
                                    intent.putExtra("pass",password);

                                    startActivity(intent);
                                    finish();
                                } else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại";
                                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    private void anhXa() {
        txtemailDK = findViewById(R.id.txtemailDK);
        txtpassDK = findViewById(R.id.txtpassDK);
        txtpassxacnhan = findViewById(R.id.txtpassxacnhan);
        btnDangKy = findViewById(R.id.btnDangKy);
        txtten = findViewById(R.id.txtten);
        txtnamsinh = findViewById(R.id.txtnamsinh);
    }

}