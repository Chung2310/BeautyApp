package com.example.beautyapp.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.adapter.ProductAdapter;
import com.example.beautyapp.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GalleryViewModel galleryViewModel;
    private ProductAdapter productAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        // Khởi tạo adapter và layout
        productAdapter = new ProductAdapter();
        binding.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewProducts.setAdapter(productAdapter);

        // Quan sát dữ liệu từ ViewModel
        galleryViewModel.getProductList().observe(getViewLifecycleOwner(), products -> {
            productAdapter.setProductList(products);
            binding.swipeRefreshLayout.setRefreshing(false);
        });

        // Tìm kiếm sản phẩm
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.filter(newText);
                return true;
            }
        });

        // Kéo để làm mới danh sách
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            galleryViewModel.loadProducts();
        });

        // Flipper và ẩn/hiện khi cuộn
        slide();
        binding.recyclerViewProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 20) {
                    binding.viewFlipper.setVisibility(View.GONE);
                } else if (dy < -20) {
                    binding.viewFlipper.setVisibility(View.VISIBLE);
                }
            }
        });

        return root;
    }

    private void slide() {
        int[] images = { R.drawable.ban1, R.drawable.ban2, R.drawable.ban3, R.drawable.ban4 };
        for (int image : images) {
            ImageView imageView = new ImageView(getContext());
            imageView.setBackgroundResource(image);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            binding.viewFlipper.addView(imageView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
