package com.example.beautyapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beautyapp.model.Order;
import com.example.beautyapp.model.OrderModel;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OrderHistoryViewModel extends ViewModel {
    private Api api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Order>> orderList = new MutableLiveData<>();

    public LiveData<List<Order>> getOrderList() {
        return orderList;
    }

    public void fetchOrderHistory(String userId) {
        compositeDisposable.add(api.getOrder(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        orderModel -> {
                            if (orderModel.isSuccess()) {
                                orderList.setValue(orderModel.getResult());
                            } else {
                                Log.e("OrderHistoryVM", "Fetch failed: " + orderModel.getMessage());
                            }
                        },
                        throwable -> Log.e("OrderHistoryVM", "Error: " + throwable.getMessage())
                ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
