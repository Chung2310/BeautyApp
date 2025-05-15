package com.example.beautyapp.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.adapter.ProductAdapter;
import com.example.beautyapp.databinding.FragmentGalleryBinding;

import java.util.List;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GalleryViewModel galleryViewModel;
    private ProductAdapter productAdapter;
    private ViewFlipper viewFlipper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        viewFlipper = binding.viewFlipper;
        slide();

        productAdapter = new ProductAdapter();
        binding.recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.recyclerViewProducts.setAdapter(productAdapter);

        galleryViewModel.getProductList().observe(getViewLifecycleOwner(), products -> {
            productAdapter.setProductList(products);
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            galleryViewModel.loadProducts();
        });
        binding.recyclerViewProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 20) { // Cuộn xuống
                    viewFlipper.setVisibility(View.GONE);
                } else if (dy < -20) { // Cuộn lên
                    viewFlipper.setVisibility(View.VISIBLE);
                }
            }
        });
        return root;
    }

    private void slide(){
        int[] images = { R.drawable.ban1, R.drawable.ban2, R.drawable.ban3, R.drawable.ban4 };
        for (int image : images) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(image);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewFlipper.addView(imageView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}