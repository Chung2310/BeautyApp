package com.example.beautyapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.example.beautyapp.model.Cart;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Cart> cartList;
    private OnCartActionListener listener;

    public interface OnCartActionListener {
        void onQuantityChanged(Cart item, int newQuantity);
        void onItemDeleted(Cart item);
    }

    public CartAdapter(List<Cart> cartList, OnCartActionListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    public void setCartList(List<Cart> list) {
        this.cartList = list;
        notifyDataSetChanged();
    }

    public List<Cart> getCartList() {
        return cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart item = cartList.get(position);
        holder.txtName.setText(item.getName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.txtPrice.setText("Giá: "+decimalFormat.format(Double.parseDouble(String.valueOf(item.getPrice())))+ "Đ");
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));

        Log.d("cartadapter", String.valueOf(item.getId()));

        Glide.with(holder.itemView.getContext())
                .load(item.getImage())
                .into(holder.imgProduct);

        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            notifyItemChanged(position);
            listener.onQuantityChanged(item, newQuantity);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                item.setQuantity(newQuantity);
                notifyItemChanged(position);
                listener.onQuantityChanged(item, newQuantity);
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemDeleted(item);
                CompositeDisposable compositeDisposable = new CompositeDisposable();
                Api api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);

                compositeDisposable.add(api.deleteDetailCart(item.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                messageModel -> {
                                    Toast.makeText(v.getContext(), "Đã xoá sản phẩm khỏi giỏ hàng",Toast.LENGTH_LONG).show();
                                },throwable -> {
                                    Log.d("cartadapter",throwable.getMessage());
                                }
                        ));
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgDelete;
        TextView txtName, txtPrice, txtQuantity;
        AppCompatButton btnIncrease, btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            txtName = itemView.findViewById(R.id.txtName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }
}
