package com.example.beautyapp.activity;

import android.app.DatePickerDialog;
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
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKiActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextInputEditText txtemailDK, txtpassDK, txtpassxacnhan, txtten;
    private AppCompatButton btnDangKy, btnAddDate;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CompositeDisposable compositeDisposable;
    private Api api;
    private String date;

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
        compositeDisposable = new CompositeDisposable();
        firebaseAuth = FirebaseAuth.getInstance();
        api = RetrofitClient.getInstance(Utils
                .BASE_URL).create(Api.class);
        anhXa();

        btnDangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtemailDK.getText().toString().trim();
                String password = txtpassDK.getText().toString().trim();
                String passwordXacnhan = txtpassxacnhan.getText().toString().trim();
                String hoten = txtten.getText().toString().trim();


                if (email.isEmpty() || password.isEmpty() || passwordXacnhan.isEmpty() || hoten.isEmpty() || date.isEmpty()) {
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
                                    String user_id = user.getUid();
                                    Log.d("dangky","Đăng ký thành công"+user_id);
                                    //tạo người dùng trong mysql
                                    compositeDisposable.add(api.addUser(user_id,email,password,hoten,date)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(
                                                    messageModel -> {

                                                        if (messageModel.isSuccess()) {
                                                            Intent intent = new Intent(DangKiActivity.this, DangNhapActivity.class);
                                                            intent.putExtra("email", email);
                                                            intent.putExtra("pass", password);
                                                            startActivity(intent);
                                                            Log.d("dangky", messageModel.getMessage());
                                                        } else {
                                                            Log.d("dangky", messageModel.getMessage());
                                                        }
                                                    },throwable -> {
                                                        Log.d("loidangky",throwable.getMessage());
                                                    }
                                            )
                                    );




                                    finish();
                                } else {
                                    String errorMessage = task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại";
                                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        btnAddDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        btnAddDate.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

    }

    private void anhXa() {
        txtemailDK = findViewById(R.id.txtemailDK);
        txtpassDK = findViewById(R.id.txtpassDK);
        txtpassxacnhan = findViewById(R.id.txtpassxacnhan);
        btnDangKy = findViewById(R.id.btnDangKy);
        txtten = findViewById(R.id.txtten);
        btnAddDate = findViewById(R.id.btnAddDate);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}