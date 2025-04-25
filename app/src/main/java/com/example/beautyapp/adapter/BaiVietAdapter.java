package com.example.beautyapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautyapp.R;
import com.example.beautyapp.model.BaiViet;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaiVietAdapter extends RecyclerView.Adapter<BaiVietAdapter.MyViewHolder> {

    Context context;
    List<BaiViet> itemList;

    @NonNull
    @Override
    public BaiVietAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_baiviet,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaiVietAdapter.MyViewHolder holder, int position) {
        BaiViet baiViet = itemList.get(position);
        holder.imageItemBaiViet.setImageResource(Integer.parseInt(baiViet.getImage()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView imageItemBaiViet;
        TextView nameUserItemBaiViet,noidungItem,soLikeItem;
        ImageView likeItem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItemBaiViet = itemView.findViewById(R.id.imageItemBaiViet);
            nameUserItemBaiViet = itemView.findViewById(R.id.nameUserItemBaiViet);
            noidungItem = itemView.findViewById(R.id.noidungItem);
            likeItem = itemView.findViewById(R.id.likeItem);
            soLikeItem = itemView.findViewById(R.id.soLikeItem);
        }
    }
}
