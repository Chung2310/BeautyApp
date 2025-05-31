package com.example.beautyapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.beautyapp.ui.gallery.ProductListFragment;

import java.util.ArrayList;
import java.util.List;

public class ProductCategoryPagerAdapter extends FragmentStateAdapter {

    private final List<ProductListFragment> fragmentList = new ArrayList<>();

    public ProductCategoryPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
        fragmentList.add(ProductListFragment.newInstance("Tất cả"));
        fragmentList.add(ProductListFragment.newInstance("Chăm sóc da mặt"));
        fragmentList.add(ProductListFragment.newInstance("Chăm sóc cơ thể"));
        fragmentList.add(ProductListFragment.newInstance("Giải pháp làn da"));
        fragmentList.add(ProductListFragment.newInstance("Chăm sóc tóc - da đầu"));
        fragmentList.add(ProductListFragment.newInstance("Mỹ phẩm trang điểm"));
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public void filter(String query) {
        for (ProductListFragment fragment : fragmentList) {
            fragment.filter(query);
        }
    }
}