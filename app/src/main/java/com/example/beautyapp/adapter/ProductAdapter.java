package com.example.beautyapp.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.example.beautyapp.activity.DetailProductActivity;
import com.example.beautyapp.interface_click.ItemClickListener;
import com.example.beautyapp.model.Product;
import com.example.beautyapp.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList = new ArrayList<>();

    public void setProductList(List<Product> products) {
        this.productList = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        ProductViewHolder myViewHolder = (ProductViewHolder) holder;

        Product p = productList.get(position);
        Log.d("adapter product",p.toString());
        holder.name.setText(p.getName());
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        holder.price.setText("Giá: "+decimalFormat.format(Double.parseDouble(p.getPrice()))+ "Đ");
        holder.stock.setText("Còn lại: " + p.getStock());

        if(p.getImage().get(0).contains("https")){
            Glide.with(holder.itemView.getContext()).load(p.getImage().get(0)).into(holder.image);
        }
        else {
            String hinh = Utils.BASE_URL+"image/"+p.getImage().get(0);
            Glide.with(holder.itemView.getContext()).load(hinh).into(holder.image);
        }
        myViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int pos, boolean isLongClick) {
                if(!isLongClick){
                    Intent intent = new Intent(holder.itemView.getContext(), DetailProductActivity.class);
                    intent.putExtra("product", p);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView name, price, stock;
        private ImageView image;
        private ItemClickListener itemClickListener;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textName);
            price = itemView.findViewById(R.id.textPrice);
            stock = itemView.findViewById(R.id.textStock);
            image = itemView.findViewById(R.id.imageProduct);
            itemView.setOnClickListener(this);
        }
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v ,getAdapterPosition(),false);
        }
    }
}
