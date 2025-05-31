package com.example.beautyapp.ui.home;

import static androidx.camera.core.impl.utils.ContextUtil.getApplicationContext;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.beautyapp.R;
import com.example.beautyapp.activity.DangBaiActivity;
import com.example.beautyapp.activity.UserActivity;
import com.example.beautyapp.adapter.BaiVietAdapter;
import com.example.beautyapp.databinding.FragmentHomeBinding;
import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.model.User;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.example.beautyapp.ui.home.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private BaiVietAdapter adapter;
    private List<BaiViet> baiVietList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private Handler handler = new Handler();
    private CompositeDisposable compositeDisposable;
    private Api api;
    private User user;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Paper.init(getContext());

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        compositeDisposable = new CompositeDisposable();
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        firebaseAuth = FirebaseAuth.getInstance();

        getUser(firebaseAuth.getUid());
        setupRecyclerView();
        setupObservers();
        setupEvents();

        if (homeViewModel.getBaiVietList().getValue().isEmpty()) {
            homeViewModel.fetchData();
        }

        binding.imageHomeFrament.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    void loadImage(String image) {
        String imageUrl;
        if (image != null && image.contains("https")) {
            imageUrl = image;
        } else {
            imageUrl = Utils.BASE_URL + "avt/" + image;
        }

        Glide.with(getContext())
                .load(imageUrl)
                .placeholder(R.drawable.android)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(binding.imageHomeFrament);
    }


    void getUser(String userId){
        compositeDisposable.add(api.getUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        userModel -> {
                            user = userModel.getResult();
                            loadImage(user.getImage());
                            Log.d("anhmain",user.getImage());
                            Paper.book().write("user_current",user);
                        }
                ));
    }

    private void setupRecyclerView() {
        linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvHomeSections.setLayoutManager(linearLayoutManager);
        adapter = new BaiVietAdapter(getContext(), baiVietList);
        binding.rvHomeSections.setAdapter(adapter);

        binding.rvHomeSections.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (!isLoading && linearLayoutManager.findLastCompletelyVisibleItemPosition() == baiVietList.size() - 1) {
                    isLoading = true;
                    loadMore();
                }
            }
        });
    }

    private void setupObservers() {
        homeViewModel.getBaiVietList().observe(getViewLifecycleOwner(), list -> {
            baiVietList.clear();
            baiVietList.addAll(list);
            adapter.notifyDataSetChanged();
        });
    }

    private void setupEvents() {
        binding.btnDangBai.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DangBaiActivity.class);
            startActivity(intent);
        });
    }

    private void loadMore() {
        baiVietList.add(null);
        adapter.notifyItemInserted(baiVietList.size() - 1);

        handler.postDelayed(() -> {
            baiVietList.remove(baiVietList.size() - 1);
            adapter.notifyItemRemoved(baiVietList.size());

            homeViewModel.nextPage();
            homeViewModel.fetchData();
            isLoading = false;
        }, 1500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
