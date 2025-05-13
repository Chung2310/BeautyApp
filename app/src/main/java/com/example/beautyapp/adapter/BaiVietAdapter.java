package com.example.beautyapp.adapter;

import static androidx.camera.core.impl.utils.ContextUtil.getApplicationContext;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.utils.Utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaiVietAdapter extends RecyclerView.Adapter<BaiVietAdapter.MyViewHolder> {

    Context context;
    List<BaiViet> itemList;

    public BaiVietAdapter(Context context, List<BaiViet> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public BaiVietAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_baiviet,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaiVietAdapter.MyViewHolder holder, int position) {
        BaiViet baiViet = itemList.get(position);

        if(baiViet.getImage().contains("https")){
            Glide.with(context).load(baiViet.getImage()).into(holder.imageBaiViet);
        }
        else {
            String hinh = Utils.BASE_URL+"avt/"+baiViet.getImage();
            Glide.with(context).load(hinh).into(holder.imageBaiViet);
        }

        if(baiViet.getLinkImage().contains("https")){
            Glide.with(context).load(baiViet.getLinkImage()).placeholder(R.drawable.android).into(holder.imageItemBaiViet);
        }
        else {
            String hinh = Utils.BASE_URL+"avt/"+baiViet.getLinkImage();
            Glide.with(context).load(hinh).placeholder(R.drawable.android).into(holder.imageItemBaiViet);
        }
        holder.nameUserItemBaiViet.setText(baiViet.getName());
        holder.noidungItem.setText(baiViet.getContent());
        holder.soLikeItem.setText(baiViet.getNumberLike().toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            Duration duration = Duration.between(baiViet.getTime(), now);
            if(duration.toDays() > 1){
                holder.timeItemBaiViet.setText(duration.toDays()+" ngày trước");
            } else if ((duration.toHours()%24) > 1) {
                holder.timeItemBaiViet.setText((duration.toHours()%24)+" giờ trước");
            } else if ((duration.toMinutes()%60)>1) {
                holder.timeItemBaiViet.setText((duration.toMinutes()%60)+" phút trước");
            } else {
                holder.timeItemBaiViet.setText((duration.getSeconds()%60)+" giây trước");
            }
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void updateItems(List<BaiViet> baiVietList){
        BaiVietDiffCallBack baiVietDiffCallBack = new BaiVietDiffCallBack(itemList,baiVietList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(baiVietDiffCallBack);

        this.itemList.clear();
        this.itemList.addAll(baiVietList);

        diffResult.dispatchUpdatesTo(this);
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        CircleImageView imageItemBaiViet;
        TextView nameUserItemBaiViet,noidungItem,soLikeItem,timeItemBaiViet;
        ImageView likeItem,imageBaiViet;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItemBaiViet = itemView.findViewById(R.id.imageItemBaiViet);
            nameUserItemBaiViet = itemView.findViewById(R.id.nameUserItemBaiViet);
            noidungItem = itemView.findViewById(R.id.noidungItem);
            likeItem = itemView.findViewById(R.id.likeItem);
            soLikeItem = itemView.findViewById(R.id.soLikeItem);
            timeItemBaiViet = itemView.findViewById(R.id.timeItemBaiViet);
            imageBaiViet = itemView.findViewById(R.id.imageBaiViet);
        }
    }
}
