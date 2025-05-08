package com.example.beautyapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.example.beautyapp.adapter.BaiVietAdapter;
import com.example.beautyapp.databinding.FragmentHomeBinding;
import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ViewFlipper viewFlipper;
    private RecyclerView recyclerView;
    private Api api;
    private CompositeDisposable compositeDisposable;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        compositeDisposable = new CompositeDisposable();

        anhXa();
        control();

        try {
            compositeDisposable.add(api.getAllArticle(Utils.user_current.getUser_id())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            baiVietModel -> {
                                if(baiVietModel.isSuccess()){
                                    List<BaiViet> baiVietList = baiVietModel.getResult();
                                    BaiVietAdapter adapter = new BaiVietAdapter(getContext(),baiVietList);
                                    recyclerView.setAdapter(adapter);
                                }
                            },throwable -> {
                                Log.d("loiketnoiserver",throwable.getMessage());
                                Toast.makeText(getContext(),throwable.getMessage(),Toast.LENGTH_LONG).show();
                            }

                    ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



        return root;
    }

    private void control() {


    }

    private void anhXa() {
        recyclerView = binding.rvHomeSections;
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}