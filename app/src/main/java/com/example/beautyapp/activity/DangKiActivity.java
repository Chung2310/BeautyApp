package com.example.beautyapp.activity;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.app.NotificationCompat;
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

// ... các package import giữ nguyên

public class DangKiActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText txtemailDK, txtpassDK, txtpassxacnhan, txtten;
    private CheckBox checkboxTerms;
    private Button btnDangKy, btnAddDate;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CompositeDisposable compositeDisposable;
    private Api api;
    private String date = "";
    private static final String CHANNEL_ID = "my_channel_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dang_ki);

        compositeDisposable = new CompositeDisposable();
        firebaseAuth = FirebaseAuth.getInstance();
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        anhXa();

        btnDangKy.setOnClickListener(v -> {
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

            if (!checkboxTerms.isChecked()) {
                Toast.makeText(getApplicationContext(), "Vui lòng chấp nhận Điều khoản và Chính sách", Toast.LENGTH_LONG).show();
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(DangKiActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification()
                                        .addOnCompleteListener(verifyTask -> {
                                            if (verifyTask.isSuccessful()) {
                                                String user_id = user.getUid();
                                                Log.d("dangky", "Đăng ký thành công " + user_id);

                                                compositeDisposable.add(api.addUser(user_id, email, password, hoten, date)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(
                                                                messageModel -> {
                                                                    Log.d("date", date);
                                                                    if (messageModel.isSuccess()) {
                                                                        Toast.makeText(this,"Kiểm tra email để xác nhận tài khoản nhé",Toast.LENGTH_LONG).show();
                                                                        showNotification("Thông báo!!!","Đăng ký thành công. Vui lòng xác minh email trước khi đăng nhập.");
                                                                        Intent intent = new Intent(DangKiActivity.this, DangNhapActivity.class);
                                                                        intent.putExtra("email", email);
                                                                        intent.putExtra("pass", password);
                                                                        startActivity(intent);
                                                                        firebaseAuth.signOut();
                                                                    } else {
                                                                        Log.d("dangky", messageModel.getMessage());
                                                                    }
                                                                },
                                                                throwable -> Log.d("loidangky", throwable.getMessage())
                                                        ));
                                            } else {
                                                Toast.makeText(getApplicationContext(),
                                                        "Không thể gửi email xác nhận. Vui lòng thử lại.",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        } else {
                            String errorMessage = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Đăng ký thất bại";
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
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

    private void showNotification(String title, String content) {
        // Tạo NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.amthanhthongbao);

        // Tạo NotificationChannel (chỉ cần với Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Tên kênh thông báo",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Mô tả kênh thông báo");
            notificationManager.createNotificationChannel(channel);
        }

        // Tạo Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notificationbell)
                .setContentTitle(title)
                .setContentText(content)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }

    private void anhXa() {
        txtemailDK = findViewById(R.id.txtemailDK);
        txtpassDK = findViewById(R.id.txtpassDK);
        txtpassxacnhan = findViewById(R.id.txtpassxacnhan);
        btnDangKy = findViewById(R.id.btnDangKy);
        txtten = findViewById(R.id.txtten);
        btnAddDate = findViewById(R.id.btnAddDate);
        checkboxTerms = findViewById(R.id.checkboxTerms);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
