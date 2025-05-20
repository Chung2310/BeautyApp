package com.example.beautyapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beautyapp.R;
import com.example.beautyapp.adapter.ImageSliderAdapter;
import com.example.beautyapp.model.Product;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DetailProductActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView textProductName,textProductPrice,textProductStock,textProductDescription;
    private Button buttonAddToCart;
    private CompositeDisposable compositeDisposable;
    private Api api;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_product);
        compositeDisposable = new CompositeDisposable();
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);

        anhXa();

        Product product = (Product) getIntent().getSerializableExtra("product");

        Paper.init(this);

        List<String> imageUrls = product.getImage();

        ImageSliderAdapter adapter = new ImageSliderAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);

        buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQuantityBottomSheet(product);
            }
        });

        show(product);
    }

    private void showQuantityBottomSheet(Product product) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_quantity, null);
        bottomSheetDialog.setContentView(view);

        TextView tvProductName = view.findViewById(R.id.tvProductName);
        EditText etQuantity = view.findViewById(R.id.etQuantity);
        AppCompatButton btnConfirm = view.findViewById(R.id.btnConfirm);

        tvProductName.setText(product.getName());

        btnConfirm.setOnClickListener(v -> {
            String quantityStr = etQuantity.getText().toString().trim();
            if (quantityStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số lượng", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                Toast.makeText(this, "Số lượng phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();

            String userId = Paper.book().read("userId");

            compositeDisposable.add(api.addCart(userId,quantity,product.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                Log.d("detail_product",messageModel.getMessage());

                            },throwable -> {
                                Log.d("detail_product",throwable.getMessage());
                            }
                    ));

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }


    private void show(Product product){
        textProductName.setText(product.getName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        textProductPrice.setText("Giá: "+decimalFormat.format(Double.parseDouble(product.getPrice()))+ "Đ");
        textProductStock.setText("Còn lại: "+product.getStock());
        textProductDescription.setText("Mô tả: "+product.getDescription());
    }

    private void anhXa() {
        viewPager = findViewById(R.id.viewPagerImages);
        textProductName = findViewById(R.id.textProductName);
        textProductPrice = findViewById(R.id.textProductPrice);
        textProductStock = findViewById(R.id.textProductStock);
        textProductDescription = findViewById(R.id.textProductDescription);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);
    }
}