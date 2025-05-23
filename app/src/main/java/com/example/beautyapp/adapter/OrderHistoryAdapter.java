package com.example.beautyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.model.Order;

import java.text.DecimalFormat;
import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvOrderId.setText("Mã đơn: #" + order.getOrderId());
        holder.tvDate.setText("Ngày đặt: " + order.getTime_order());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.tvTotal.setText("Tổng tiền: "+decimalFormat.format(Double.parseDouble(order.getTotalamount()))+ "Đ");

        holder.tvPhone.setText("SĐT: " + order.getPhone());
        holder.tvAddress.setText("Địa chỉ: " + order.getAddress());

        // Gắn adapter sản phẩm
        ProductOrderAdapter productAdapter = new ProductOrderAdapter(context, order.getItems());
        holder.rvProductList.setLayoutManager(new LinearLayoutManager(context));
        holder.rvProductList.setAdapter(productAdapter);
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvDate, tvTotal, tvPhone, tvAddress;
        RecyclerView rvProductList;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            rvProductList = itemView.findViewById(R.id.rvProductList);
        }
    }
}
