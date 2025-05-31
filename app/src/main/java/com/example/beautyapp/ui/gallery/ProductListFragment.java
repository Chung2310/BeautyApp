package com.example.beautyapp.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.beautyapp.adapter.ProductAdapter;
import com.example.beautyapp.databinding.FragmentProductListBinding;

public class ProductListFragment extends Fragment {

    private static final String ARG_CATEGORY = "category";

    private String category;
    private FragmentProductListBinding binding;
    private ProductListViewModel viewModel;
    private ProductAdapter adapter;

    public static ProductListFragment newInstance(String category) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }

        adapter = new ProductAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ProductListViewModel.class);
        viewModel.setCategory(category);

        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.setProductList(products);
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.loadProducts();
        });
    }

    // Phương thức filter được gọi từ PagerAdapter
    public void filter(String query) {
        adapter.filter(query);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
