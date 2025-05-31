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
import com.example.beautyapp.adapter.ProductCategoryPagerAdapter;
import com.example.beautyapp.databinding.FragmentGalleryBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private ProductCategoryPagerAdapter pagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        pagerAdapter = new ProductCategoryPagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Tất cả");
                            break;
                        case 1:
                            tab.setText("Chăm sóc da mặt");
                            break;
                        case 2:
                            tab.setText("Chăm sóc cơ thể");
                            break;
                        case 3:
                            tab.setText("Giải pháp làn da");
                            break;
                        case 4:
                            tab.setText("Chăm sóc tóc - da đầu");
                            break;
                        case 5:
                            tab.setText("Mỹ phẩm trang điểm");
                            break;
                    }
                }).attach();

        slide();

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pagerAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pagerAdapter.filter(newText);
                return true;
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
