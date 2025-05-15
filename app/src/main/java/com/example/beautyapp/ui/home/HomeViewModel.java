package com.example.beautyapp.ui.home;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.beautyapp.model.BaiViet;
import com.example.beautyapp.model.BaiVietModel;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomeViewModel extends AndroidViewModel {
    private final MutableLiveData<List<BaiViet>> baiVietList = new MutableLiveData<>(new ArrayList<>());
    private final Api api;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private int currentPage = 1;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
    }

    public LiveData<List<BaiViet>> getBaiVietList() {
        return baiVietList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void nextPage() {
        currentPage++;
    }

    public void resetPage() {
        currentPage = 1;
        baiVietList.setValue(new ArrayList<>());
    }

    public void fetchData() {
        compositeDisposable.add(api.getAllArticle() // cần truyền page nếu API hỗ trợ phân trang
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (BaiVietModel model) -> {
                            if (model.isSuccess()) {
                                List<BaiViet> current = baiVietList.getValue();
                                if (current != null) {
                                    current.addAll(model.getResult());
                                    baiVietList.setValue(current);
                                } else {
                                    baiVietList.setValue(model.getResult());
                                }
                            }
                        },
                        throwable -> Toast.makeText(getApplication(), "Không thể kết nối đến server", Toast.LENGTH_LONG).show()
                ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
