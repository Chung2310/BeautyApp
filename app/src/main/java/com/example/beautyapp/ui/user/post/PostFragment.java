package com.example.beautyapp.ui.user.post;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.adapter.BaiVietAdapter;
import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.auth.FirebaseAuth;

public class PostFragment extends Fragment {

    private RecyclerView recyclerView;
    private BaiVietAdapter adapter;
    private PostViewModel postViewModel;
    private LinearLayoutManager linearLayoutManager;
    private List<BaiViet> baiVietList = new ArrayList<>();
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewPost);
        linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BaiVietAdapter(requireContext(), baiVietList);
        recyclerView.setAdapter(adapter);

        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

        // Lấy userId hiện tại
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            postViewModel.resetAndFetch(userId);
        }

        // Quan sát dữ liệu
        postViewModel.getBaiVietLiveData().observe(getViewLifecycleOwner(), this::updateList);

        // Load thêm khi scroll xuống cuối
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (dy > 0) {
                    int visibleItemCount = linearLayoutManager.getChildCount();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    int pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            isLoading = true;
                            postViewModel.loadNextPage(userId);
                        }
                    }
                }
            }
        });
    }

    private void updateList(List<BaiViet> newList) {
        isLoading = false;
        baiVietList.clear();
        baiVietList.addAll(newList);
        adapter.notifyDataSetChanged();
    }
}
