package com.example.beautyapp.activity;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.beautyapp.R;
import com.example.beautyapp.model.Consultant;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BookingActivity extends AppCompatActivity {

    private ImageView imageConsultant;
    private TextView tvName,tvSelectedDate,tvSelectedTimeDen,tvSelectedTimeVe;
    private EditText etAddress,etPhone;
    private Button btnSelectDate,btnSelectTimeDen,btnSelectTimeVe;
    private Button btnBook;
    private String time1,time2,date;
    private CheckBox checkboxBooking;
    private CompositeDisposable compositeDisposable;
    private Api api;
    private FirebaseAuth firebaseAuth;
    private static final String CHANNEL_ID = "my_channel_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);

        compositeDisposable = new CompositeDisposable();

        Consultant consultant = (Consultant) getIntent().getSerializableExtra("consultant");

        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        firebaseAuth = FirebaseAuth.getInstance();

        anhXa();

        String imageUrl = consultant.getImageUrl() != null && consultant.getImageUrl().contains("https")
                ? consultant.getImageUrl()
                : Utils.BASE_URL + "image/" + consultant.getImageUrl();

        Glide.with(getApplicationContext())
                .load(imageUrl)
                .placeholder(R.drawable.android)
                .error(R.drawable.android)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageConsultant);

        tvName.setText(consultant.getName());

        btnSelectTimeDen.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, selectedHour, selectedMinute) -> {
                        time1 = selectedHour + ":" + String.format("%02d", selectedMinute);
                        tvSelectedTimeDen.setText("Giờ đã chọn: " + time1);

                    }, hour, minute, true);
            timePickerDialog.show();
        });

        btnSelectTimeVe.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    (view, selectedHour, selectedMinute) -> {
                        time2 = selectedHour + ":" + String.format("%02d", selectedMinute);
                        tvSelectedTimeVe.setText("Giờ đã chọn: " + time2);

                    }, hour, minute, true);
            timePickerDialog.show();
        });

        btnSelectDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        tvSelectedDate.setText("Ngày đã chọn: " + date);

                    }, year, month, day);
            datePickerDialog.show();
        });

        btnBook.setOnClickListener(v -> {

            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();

            if (phone.isEmpty() || address.isEmpty() || date == null || time1 == null || time2 == null) {
                Toast.makeText(getApplicationContext(), "Vui lòng nhập đầy đủ thông tin trước khi đặt lịch", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!checkboxBooking.isChecked()) {
                Toast.makeText(getApplicationContext(), "Vui lòng xác nhận đăng ký", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("\\d{10}")) {
                Toast.makeText(getApplicationContext(), "Số điện thoại phải gồm đúng 10 chữ số", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Chuẩn bị định dạng và chuyển đổi thời gian
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                Date timeStart = sdf.parse(date + " " + time1);
                Date timeEnd = sdf.parse(date + " " + time2);
                Date now = new Date();

                if (timeStart == null || timeEnd == null) {
                    Toast.makeText(getApplicationContext(), "Lỗi xử lý thời gian", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (timeStart.before(now)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng chọn thời gian sau thời điểm hiện tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!timeEnd.after(timeStart)) {
                    Toast.makeText(getApplicationContext(), "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT).show();
                    return;
                }
                Calendar calStart = Calendar.getInstance();
                calStart.setTime(timeStart);
                int hourStart = calStart.get(Calendar.HOUR_OF_DAY);

                Calendar calEnd = Calendar.getInstance();
                calEnd.setTime(timeEnd);
                int hourEnd = calEnd.get(Calendar.HOUR_OF_DAY);

                if (hourStart >= 20 || hourStart < 7 || hourEnd >= 20 || hourEnd < 7) {
                    Toast.makeText(getApplicationContext(), "Thời gian tư vấn phải nằm trong khung giờ làm việc: 07:00 - 20:00", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Gửi dữ liệu lên API
                compositeDisposable.add(api.addBooking(firebaseAuth.getUid(), phone, address, date , time1, time2, consultant.getConsultantId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                messageModel -> {
                                    Log.d("booking", messageModel.getMessage());
                                    Toast.makeText(getApplicationContext(), "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                                    showNotification("Đặt lịch thành công!","Thời gian:  "+time1+" đến "+time2+ " ngày "+ date+". Vui lòng đến đúng giờ để được bác sỹ "+consultant.getName()+" tứ vấn nhé.");
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                },
                                throwable -> {
                                    Log.d("booking", throwable.getMessage());
                                    Toast.makeText(getApplicationContext(), "Đặt lịch thất bại!", Toast.LENGTH_SHORT).show();
                                }
                        ));
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Định dạng thời gian không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void anhXa() {
        imageConsultant = findViewById(R.id.imageConsultant);
        tvName = findViewById(R.id.tvConsultantName);
        btnBook = findViewById(R.id.btnBook);
        checkboxBooking = findViewById(R.id.checkboxBooking);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        btnSelectTimeDen = findViewById(R.id.btnSelectTimeDen);
        btnSelectTimeVe = findViewById(R.id.btnSelectTimeVe);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        tvSelectedTimeDen = findViewById(R.id.tvSelectedTimeDen);
        tvSelectedTimeVe = findViewById(R.id.tvSelectedTimeVe);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);
    }

    private void showNotification(String title, String content) {
        // Tạo NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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
        // Intent để xử lý khi bấm vào thông báo
//        Intent intent = new Intent(this, ShowMSG.class); // Chuyển hướng về MainActivity
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );

        // Intent mở màn hình quay số
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:113")); // Nhập sẵn số 113

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Yêu cầu để chạy trên Android 12+
        );

        // Tạo Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background) // Icon nhỏ cho thông báo (thêm icon vào res/drawable)
                .setContentTitle(title)                   // Tiêu đề thông báo
                .setContentText(content)                 // Nội dung thông báo
                .setContentIntent(pendingIntent) //bam vao thi mo activity nao
                .setAutoCancel(true)         //bam vao thi close Thong bao
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // Đặt mức ưu tiên

        // Hiển thị thông báo
        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}