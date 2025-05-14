package com.example.beautyapp.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.utils.Utils;

import java.time.format.DateTimeFormatter;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaiVietAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private Context context;
    private List<BaiViet> baiVietList;

    public BaiVietAdapter(Context context, List<BaiViet> baiVietList) {
        this.context = context;
        this.baiVietList = baiVietList;
    }

    @Override
    public int getItemViewType(int position) {
        return baiVietList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_baiviet, parent, false);
            return new BaiVietViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BaiVietViewHolder) {
            BaiViet baiViet = baiVietList.get(position);
            BaiVietViewHolder viewHolder = (BaiVietViewHolder) holder;

            viewHolder.tvName.setText(baiViet.getName());
            viewHolder.tvContent.setText(baiViet.getContent());
            viewHolder.tvSoLike.setText(String.valueOf(baiViet.getNumberLike()));
            viewHolder.tvTime.setText(baiViet.getTime());



            if(baiViet.getImage().contains("https")){
                Glide.with(context).load(baiViet.getImage()).into(((BaiVietViewHolder) holder).imgUser);
            }
            else {
                String hinh = Utils.BASE_URL+"avt/"+baiViet.getImage();
                Glide.with(context).load(hinh).into(((BaiVietViewHolder) holder).imgUser);
            }

            if(baiViet.getLinkImage().contains("https")){
                Glide.with(context).load(baiViet.getLinkImage()).placeholder(R.drawable.android).into(((BaiVietViewHolder) holder).imgBaiViet);
            }
            else {
                String hinh = Utils.BASE_URL+"image/"+baiViet.getLinkImage();
                Glide.with(context).load(hinh).placeholder(R.drawable.android).into(((BaiVietViewHolder) holder).imgBaiViet);
            }
        }

    }

    @Override
    public int getItemCount() {
        return baiVietList == null ? 0 : baiVietList.size();
    }

    public static class BaiVietViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgUser;
        TextView tvName, tvTime, tvContent, tvSoLike;
        ImageView imgBaiViet, imgLike;

        public BaiVietViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imageItemBaiViet);
            tvName = itemView.findViewById(R.id.nameUserItemBaiViet);
            tvTime = itemView.findViewById(R.id.timeItemBaiViet);
            tvContent = itemView.findViewById(R.id.noidungItem);
            tvSoLike = itemView.findViewById(R.id.soLikeItem);
            imgBaiViet = itemView.findViewById(R.id.imageBaiViet);
            imgLike = itemView.findViewById(R.id.likeItem);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBarLoading);
        }

    }
}
