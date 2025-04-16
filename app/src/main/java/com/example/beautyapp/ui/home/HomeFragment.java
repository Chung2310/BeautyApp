package com.example.beautyapp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.example.beautyapp.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViewFlipper viewFlipper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Ánh xạ ViewFlipper từ layout
        viewFlipper = binding.viewflipperMHC;

        // Thiết lập banner
        setupBanners();

        return root;
    }

    private void setupBanners() {
        // Danh sách banner làm đẹp (thay bằng URL thực tế sau)
        List<String> beautyBanners = new ArrayList<>();
        beautyBanners.add("https://th.bing.com/th/id/OIP.Y9MaxiVxV-8HnzG7MuNC3wHaE8?rs=1&pid=ImgDetMain");
        beautyBanners.add("https://khoinguonsangtao.vn/wp-content/uploads/2022/08/hinh-anh-meo-cute-de-thuong-nhat.jpg");
        beautyBanners.add("https://th.bing.com/th/id/OIP.RcZyoSqmxYNvcTFh5rxsXQHaE7?w=2048&h=1363&rs=1&pid=ImgDetMain");

        // Thêm banner vào ViewFlipper
        for (String bannerUrl : beautyBanners) {
            ImageView imageView = new ImageView(getContext());

            // Sử dụng Glide để load ảnh
            Glide.with(this)
                    .load(bannerUrl)
                    .into(imageView);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewFlipper.addView(imageView);
        }

        // Cài đặt animation
        Animation slideIn = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
        Animation slideOut = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_right);

        viewFlipper.setInAnimation(slideIn);
        viewFlipper.setOutAnimation(slideOut);

        // Tự động chuyển sau mỗi 3 giây
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}