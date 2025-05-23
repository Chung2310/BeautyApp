package com.example.beautyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.beautyapp.R;
import com.example.beautyapp.model.ProductOrder;
import com.example.beautyapp.utils.Utils;

import java.text.DecimalFormat;
import java.util.List;

public class ProductOrderAdapter extends RecyclerView.Adapter<ProductOrderAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductOrder> productList;

    public ProductOrderAdapter(Context context, List<ProductOrder> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_order, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductOrder product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvQuantity.setText("Số lượng: " + product.getQuantity());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.tvPrice.setText("Đơn giá: "+decimalFormat.format(Double.parseDouble(product.getPrice()))+ "Đ");

        String fullImageUrl;
        if (product.getImage().startsWith("http")) {
            fullImageUrl = product.getImage();
        } else {
            fullImageUrl = Utils.BASE_URL + "avt/" + product.getImage();
        }

        Glide.with(context)
                .load(fullImageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.android)
                .skipMemoryCache(true)
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName, tvQuantity, tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}
