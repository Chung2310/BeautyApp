package com.example.beautyapp.ui.home;

import static android.view.View.VISIBLE;

import static androidx.camera.core.impl.utils.ContextUtil.getApplicationContext;
import static com.example.beautyapp.utils.Utils.user_current;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beautyapp.R;
import com.example.beautyapp.activity.DangBaiActivity;
import com.example.beautyapp.activity.UserActivity;
import com.example.beautyapp.adapter.BaiVietAdapter;
import com.example.beautyapp.databinding.FragmentHomeBinding;
import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private Api api;
    private CompositeDisposable compositeDisposable;
    private boolean isLoading = false;
    private LinearLayoutManager linearLayoutManager;
    private List<BaiViet> baiVietList = new ArrayList<>();
    private Handler handler = new Handler();
    private BaiVietAdapter adapter;
    private CircleImageView imageHomeFrament;
    private AppCompatButton btnDangBai;
    private int page = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Paper.init(getContext());

        Utils.user_current = Paper.book().read("user_current");

        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        compositeDisposable = new CompositeDisposable();

        imageHomeFrament = binding.imageHomeFrament;

        if(user_current.getImage().contains("https")){
            Glide.with(getContext()).load(user_current.getImage()).placeholder(R.drawable.android).into(imageHomeFrament);
        }
        else {
            String hinh = Utils.BASE_URL+"avt/"+user_current.getImage();
            Glide.with(getContext()).load(hinh).placeholder(R.drawable.android).into(imageHomeFrament);
        }

        anhXa();
        getData();

        btnDangBai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DangBaiActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }


    private void anhXa() {
        recyclerView = binding.rvHomeSections;
        imageHomeFrament = binding.imageHomeFrament;
        btnDangBai = binding.btnDangBai;
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void addEvenLoad() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && linearLayoutManager.findLastCompletelyVisibleItemPosition() == baiVietList.size() - 1) {
                    isLoading = true;
                    loadMore();
                }
            }
        });
    }

    private void loadMore() {
        baiVietList.add(null);
        adapter.notifyItemInserted(baiVietList.size() - 1);

        handler.postDelayed(() -> {
            baiVietList.remove(baiVietList.size() - 1);
            adapter.notifyItemRemoved(baiVietList.size());

            page++;
            getData();
            isLoading = false;
        }, 1500); // giả lập loading
    }

    private void getData() {
        compositeDisposable.add(api.getAllArticle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        baiVietModel -> {
                            if (baiVietModel.isSuccess()) {
                                Log.d("home",baiVietModel.getResult().toString());
                                if (adapter == null) {
                                    baiVietList = baiVietModel.getResult();
                                    adapter = new BaiVietAdapter(getContext(), baiVietList);
                                    recyclerView.setAdapter(adapter);
                                } else {
                                    int positionStart = baiVietList.size();
                                    List<BaiViet> newData = baiVietModel.getResult();
                                    baiVietList.addAll(newData);
                                    adapter.notifyItemRangeInserted(positionStart, newData.size());
                                }
                            } else {
                                isLoading = false;
                            }
                        },
                        throwable -> {
                            Toast.makeText(getContext(), "Không thể kết nối đến server", Toast.LENGTH_LONG).show();
                            isLoading = false;
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
