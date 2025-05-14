package com.example.beautyapp.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.beautyapp.R;
import com.example.beautyapp.adapter.ImageSliderAdapter;
import com.example.beautyapp.model.Product;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class DetailProductActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TextView textProductName,textProductPrice,textProductStock,textProductDescription;
    private Button buttonAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_product);

        anhXa();

        Product product = (Product) getIntent().getSerializableExtra("product");


        List<String> imageUrls = product.getImage();

        ImageSliderAdapter adapter = new ImageSliderAdapter(this, imageUrls);
        viewPager.setAdapter(adapter);

        show(product);
    }

    private void show(Product product){
        textProductName.setText(product.getName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        textProductPrice.setText(decimalFormat.format(Double.parseDouble(product.getPrice()))+ "Đ");
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