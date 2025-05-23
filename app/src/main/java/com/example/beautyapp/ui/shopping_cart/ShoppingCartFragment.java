package com.example.beautyapp.ui.shopping_cart;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.beautyapp.R;
import com.example.beautyapp.adapter.CartAdapter;
import com.example.beautyapp.databinding.FragmentShoppingCartBinding;
import com.example.beautyapp.model.Cart;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;
import java.util.ArrayList;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class ShoppingCartFragment extends Fragment {

    private FragmentShoppingCartBinding binding;
    private ShoppingCartViewModel viewModel;
    private CartAdapter adapter;
    private CompositeDisposable compositeDisposable;
    private Api api;
    private int total =0;

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

        binding.btnThanhToan.setOnClickListener(v -> showConfirmationDialog());


        viewModel = new ViewModelProvider(this).get(ShoppingCartViewModel.class);

        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            adapter.setCartList(items);
            updateTotalPrice();
        });

        viewModel.loadCartItems();
    }

    private void updateTotalPrice() {

        for (Cart item : adapter.getCartList()) {
            total += item.getPrice() * item.getQuantity();
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        binding.tvTongTien.setText(decimalFormat.format(total)+ "Đ");


    }

    private void showConfirmationDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_checkout_info, null);

        EditText edtAddress = dialogView.findViewById(R.id.edtAddress);
        EditText edtPhone = dialogView.findViewById(R.id.edtPhone);

        TextView title = new TextView(getContext());
        title.setText("Xác nhận thanh toán");
        title.setPadding(32, 32, 32, 16);
        title.setTextSize(20f);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(ContextCompat.getColor(requireContext(), R.color.pinkLightDark));

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setCustomTitle(title)
                .setView(dialogView)
                .setPositiveButton("Xác nhận", null) // để xử lý sau
                .setNegativeButton("Hủy", (d, w) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.pinkLightDark));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.pinkLightDark));

            // Xử lý nút xác nhận
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String address = edtAddress.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();

                if (address.isEmpty() || phone.isEmpty()) {
                    Toast.makeText(getContext(), "Vui lòng nhập đầy đủ địa chỉ và số điện thoại", Toast.LENGTH_SHORT).show();
                } else {
                    addOrder(edtAddress.getText().toString(),edtPhone.getText().toString());
                    dialog.dismiss(); // đóng dialog nếu hợp lệ
                }
            });
        });

        dialog.show();

        // Set màu nền
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.pinkLight))
        );
    }

    private void addOrder( String address, String phone) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();

        compositeDisposable = new CompositeDisposable();
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);

        String cartJson = new com.google.gson.Gson().toJson(adapter.getCartList());

        Log.d("cart",cartJson);

        Double totalItem = (double) 0;
        for (Cart item : adapter.getCartList()) {
            totalItem = item.getPrice()*item.getQuantity();
        }

        compositeDisposable.add(api.createOrder(
                                phone,
                                totalItem,
                                userId,
                                address,
                                cartJson
                        )
                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                        .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                        .subscribe(
                                userModel -> {
                                    if (userModel.isSuccess()) {
                                        Toast.makeText(getContext(), "Thanh toán thành công", Toast.LENGTH_LONG).show();
                                        adapter.setCartList(new ArrayList<>());
                                        binding.tvTongTien.setText("0Đ");


                                    } else {
                                        Toast.makeText(getContext(), "Thất bại: " + userModel.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                },
                                throwable -> {
                                    Toast.makeText(getContext(), "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                                }
                        )
        );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
