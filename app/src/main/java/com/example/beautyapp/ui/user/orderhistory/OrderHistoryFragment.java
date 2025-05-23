package com.example.beautyapp.ui.user.orderhistory;

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
import com.example.beautyapp.adapter.OrderHistoryAdapter;
import com.example.beautyapp.viewmodel.OrderHistoryViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class OrderHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderHistoryViewModel viewModel;
    private OrderHistoryAdapter adapter;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewOrderHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(OrderHistoryViewModel.class);

        // Lấy userId từ Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Gọi API lấy danh sách đơn hàng
        viewModel.fetchOrderHistory(userId);

        // Quan sát dữ liệu đơn hàng
        viewModel.getOrderList().observe(getViewLifecycleOwner(), orders -> {
            if (orders != null) {
                adapter = new OrderHistoryAdapter(requireContext(), orders);
                recyclerView.setAdapter(adapter);
            }
        });
    }
}
