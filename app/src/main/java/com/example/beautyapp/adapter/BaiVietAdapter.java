package com.example.beautyapp.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.beautyapp.R;
import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.time.format.DateTimeFormatter;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BaiVietAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    Api api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Context context;
    private List<BaiViet> baiVietList;
    private boolean isLike = false;

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
            viewHolder.tvSoLike.setText(String.valueOf((baiViet.getNumberLike())));
            viewHolder.tvTime.setText(baiViet.getTime());

            compositeDisposable.add(api.checkLike(baiViet.getId(), firebaseAuth.getUid())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            messageModel -> {
                                Log.d("BaiVietAdapter", baiViet.getId()+" "+ firebaseAuth.getUid());
                                if(messageModel.isSuccess()){
                                    viewHolder.imgLike.setBackgroundResource(R.drawable.love1);
                                    isLike = true;
                                    Log.e("BaiVietAdapter", messageModel.getMessage()+messageModel.isSuccess());
                                }
                                else {
                                    viewHolder.imgLike.setBackgroundResource(R.drawable.love);
                                    isLike = false;
                                    Log.e("BaiVietAdapter", messageModel.getMessage()+messageModel.isSuccess());
                                }
                            },
                            throwable -> Log.e("BaiVietAdapter", "Lỗi checkLike: " + throwable.getMessage())
                    ));


            if (baiViet.getImage() != null && !baiViet.getImage().isEmpty()) {
                String avtUrl = baiViet.getImage().contains("https") ?
                        baiViet.getImage() :
                        Utils.BASE_URL + "avt/" + baiViet.getImage();

                Glide.with(context).load(avtUrl).placeholder(R.drawable.android)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true).
                        into(viewHolder.imgUser);
            }

            if (baiViet.getLinkImage() != null && !baiViet.getLinkImage().isEmpty()) {
                String link = baiViet.getLinkImage();

                if (link.contains("youtube.com") || link.contains("youtu.be")) {
                    // Hiển thị WebView với video YouTube
                    viewHolder.imgBaiViet.setVisibility(View.GONE);
                    viewHolder.webView.setVisibility(View.VISIBLE);

                    viewHolder.webView.getSettings().setJavaScriptEnabled(true);
                    viewHolder.webView.getSettings().setLoadWithOverviewMode(true);
                    viewHolder.webView.getSettings().setUseWideViewPort(true);

                    // Convert YouTube link to embed format
                    String videoId = "";
                    if (link.contains("youtube.com")) {
                        int index = link.indexOf("v=");
                        if (index != -1) {
                            videoId = link.substring(index + 2);
                            int ampIndex = videoId.indexOf("&");
                            if (ampIndex != -1) {
                                videoId = videoId.substring(0, ampIndex);
                            }
                        }
                    } else if (link.contains("youtu.be")) {
                        int index = link.lastIndexOf("/");
                        if (index != -1) {
                            videoId = link.substring(index + 1);
                        }
                    }

                    String iframe = "<html><body style='margin:0'>" +
                            "<iframe width='100%' height='100%' " +
                            "src='https://www.youtube.com/embed/" + videoId + "' " +
                            "frameborder='0' allowfullscreen></iframe>" +
                            "</body></html>";

                    viewHolder.webView.loadData(iframe, "text/html", "utf-8");

                } else {
                    // Hiển thị ảnh nếu không phải YouTube
                    viewHolder.imgBaiViet.setVisibility(View.VISIBLE);
                    viewHolder.webView.setVisibility(View.GONE);

                    String postImgUrl = link.contains("https") ?
                            link : Utils.BASE_URL + "images/" + link;

                    Glide.with(context)
                            .load(postImgUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .placeholder(R.drawable.android)
                            .into(viewHolder.imgBaiViet);
                }
            } else {
                // Ẩn cả ảnh và webview nếu không có link
                viewHolder.imgBaiViet.setVisibility(View.GONE);
                viewHolder.webView.setVisibility(View.GONE);
            }



            if(isLike == false){
                viewHolder.imgLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (viewHolder.imgLike.getBackground().getConstantState() ==
                                context.getResources().getDrawable(R.drawable.love1).getConstantState()) {
                            return;
                        }

                        compositeDisposable.add(api.setLike(baiViet.getId(),firebaseAuth.getUid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        messageModel -> {
                                            isLike = true;
                                            baiViet.setNumberLike(baiViet.getNumberLike()+1);
                                            viewHolder.tvSoLike.setText(String.valueOf(baiViet.getNumberLike()));
                                            Log.d("baivietadapter","done");
                                        }, throwable -> {
                                            Log.d("baivietadapter",throwable.getMessage());
                                        }
                                ));
                    }
                });
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
        WebView webView;

        public BaiVietViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imageItemBaiViet);
            tvName = itemView.findViewById(R.id.nameUserItemBaiViet);
            tvTime = itemView.findViewById(R.id.timeItemBaiViet);
            tvContent = itemView.findViewById(R.id.noidungItem);
            tvSoLike = itemView.findViewById(R.id.soLikeItem);
            imgBaiViet = itemView.findViewById(R.id.imageBaiViet);
            imgLike = itemView.findViewById(R.id.likeItem);
            webView = itemView.findViewById(R.id.webViewYoutube);
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
