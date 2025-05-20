package com.example.beautyapp.ui.shopping_cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.beautyapp.adapter.CartAdapter;
import com.example.beautyapp.databinding.FragmentShoppingCartBinding;
import com.example.beautyapp.model.Cart;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ShoppingCartFragment extends Fragment {

    private FragmentShoppingCartBinding binding;
    private ShoppingCartViewModel viewModel;
    private CartAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShoppingCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new CartAdapter(new ArrayList<>(), new CartAdapter.OnCartActionListener() {
            @Override
            public void onQuantityChanged(Cart item, int newQuantity) {
                viewModel.updateQuantity(item.getProductId(), newQuantity);
                updateTotalPrice();
            }

            @Override
            public void onItemDeleted(Cart item) {
                viewModel.deleteCartItem(item.getProductId());
                updateTotalPrice();
            }
        });

        binding.recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerCart.setAdapter(adapter);

        binding.btnThanhToan.setOnClickListener(v -> {
            // TODO: xử lý thanh toán
        });

        viewModel = new ViewModelProvider(this).get(ShoppingCartViewModel.class);

        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setCartList(items);
            updateTotalPrice();
        });

        viewModel.loadCartItems();
    }

    private void updateTotalPrice() {
        int total = 0;
        for (Cart item : adapter.getCartList()) {
            total += item.getPrice() * item.getQuantity();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        binding.tvTongTien.setText(decimalFormat.format(total)+ "Đ");


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
