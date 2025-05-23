package com.example.beautyapp.ui.user.post;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PostViewModel extends ViewModel {

    private MutableLiveData<List<BaiViet>> baiVietLiveData = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Api api;
    private int currentPage = 1;
    private boolean isLoading = false;

    public PostViewModel() {
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        baiVietLiveData.setValue(new ArrayList<>());
    }

    public LiveData<List<BaiViet>> getBaiVietLiveData() {
        return baiVietLiveData;
    }

    public void fetchPosts(String userId, int page) {
        if (isLoading) return;  // tránh gọi đồng thời
        isLoading = true;

        compositeDisposable.add(
                api.getAllArticleUser(userId, page)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(baiVietModel -> {
                            if (baiVietModel.isSuccess()) {
                                List<BaiViet> currentList = baiVietLiveData.getValue();
                                if (page == 1) {
                                    currentList = new ArrayList<>();
                                }
                                currentList.addAll(baiVietModel.getResult());
                                baiVietLiveData.setValue(currentList);
                            }
                            isLoading = false;
                        }, throwable -> {
                            // Xử lý lỗi nếu cần
                            isLoading = false;
                        })
        );
    }

    public void loadNextPage(String userId) {
        currentPage++;
        fetchPosts(userId, currentPage);
    }

    public void resetAndFetch(String userId) {
        currentPage = 1;
        fetchPosts(userId, currentPage);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
