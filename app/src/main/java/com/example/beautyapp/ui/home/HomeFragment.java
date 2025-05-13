package com.example.beautyapp.ui.home;

import static android.view.View.VISIBLE;

import static androidx.camera.core.impl.utils.ContextUtil.getApplicationContext;
import static com.example.beautyapp.utils.Utils.user_current;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.example.beautyapp.adapter.BaiVietAdapter;
import com.example.beautyapp.databinding.FragmentHomeBinding;
import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;

import com.example.beautyapp.utils.Utils;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CircleImageView circleImgaeHome;
    private AppCompatButton btnHome;
    private ViewFlipper viewFlipper;
    private RecyclerView recyclerView;
    private Api api;
    private CompositeDisposable compositeDisposable;
    private TextView textViewHomeFrament;
    boolean isLoading = false;
    private LinearLayoutManager linearLayoutManager;
    private List<BaiViet> baiVietList;
    private Handler handler = new Handler();
    private BaiVietAdapter adapter;
    private int page = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        compositeDisposable = new CompositeDisposable();

        anhXa();
        loadTT();


        return root;
    }


    private void loadTT() {

        Glide.with(this)
                .load(user_current.getImage())
                .placeholder(R.drawable.android)
                .into(circleImgaeHome);

        addEvenLoad();
    }


    private void anhXa() {
        recyclerView = binding.rvHomeSections;
        textViewHomeFrament = binding.textViewHomeFrament;
    

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void addEvenLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isLoading == false){
                    if(linearLayoutManager.findLastCompletelyVisibleItemPosition() == baiVietList.size()-1){
                        isLoading = true;
                        loadMore();
                    }
                }
            }
        });
    }

    private void loadMore(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                baiVietList.add(null);
                adapter.notifyItemInserted(baiVietList.size()-1);
            }
        });
        handler.post(new Runnable() {
            @Override
            public void run() {
                baiVietList.remove(baiVietList.size()-1);
                adapter.notifyItemRemoved(baiVietList.size());
                page=page+1;
                getData(page);
                adapter.notifyDataSetChanged();
                isLoading = false;
            }
        });
    }
    private void getData(int page) {
        compositeDisposable.add(api.getAllArticle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        baiVietModel -> {
                            if(baiVietModel.isSuccess()){
                                if(adapter == null){
                                    baiVietList = baiVietModel.getResult();
                                    adapter = new BaiVietAdapter(getContext(),baiVietList);
                                    recyclerView.setAdapter(adapter);
                                }
                                else {
                                    int vitri = baiVietList.size()-1;
                                    int soluongadd = baiVietModel.getResult().size();
                                    for(int i=0;i<soluongadd;i++){
                                        baiVietList.add(baiVietModel.getResult().get(i));
                                    }
                                    adapter.notifyItemRangeInserted(vitri,soluongadd);
                                }
                            } else {
                                isLoading = true;
                            }
                        },throwable -> {
                            Toast.makeText(getContext(),"Không thể kết nối đến sever",Toast.LENGTH_LONG).show();
                        }
                ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}