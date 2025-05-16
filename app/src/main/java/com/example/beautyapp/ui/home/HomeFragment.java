package com.example.beautyapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.beautyapp.R;
import com.example.beautyapp.activity.DangBaiActivity;
import com.example.beautyapp.activity.UserActivity;
import com.example.beautyapp.adapter.BaiVietAdapter;
import com.example.beautyapp.databinding.FragmentHomeBinding;
import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.utils.Utils;
import com.example.beautyapp.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private BaiVietAdapter adapter;
    private List<BaiViet> baiVietList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private boolean isLoading = false;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Paper.init(getContext());
        Utils.user_current = Paper.book().read("user_current");

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupUserInfo();
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

    private void setupUserInfo() {
        if (Utils.user_current != null) {
            String imageUrl = Utils.user_current.getImage().contains("https")
                    ? Utils.user_current.getImage()
                    : Utils.BASE_URL + "avt/" + Utils.user_current.getImage();

            Glide.with(getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.android)
                    .into(binding.imageHomeFrament);
        }
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
