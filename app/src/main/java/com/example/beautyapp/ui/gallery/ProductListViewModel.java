package com.example.beautyapp.ui.gallery;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beautyapp.model.Product;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductListViewModel extends ViewModel {

    private final MutableLiveData<List<Product>> products = new MutableLiveData<>(new ArrayList<>());
    private String category;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void setCategory(String category) {
        this.category = category;
        loadProducts();
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public void loadProducts() {
        if (category == null || category.isEmpty()) return;

        Api api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);

        compositeDisposable.add(
                api.getProductCategory(category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                productModel -> {
                                    if (productModel != null && productModel.getResult() != null) {
                                        products.setValue(productModel.getResult());
                                        Log.e("ProductListViewModel", category);
                                        Log.e("ProductListViewModel", products.getValue().get(0).getName());
                                    } else {
                                        products.setValue(new ArrayList<>());
                                    }
                                },
                                throwable -> {
                                    Log.e("ProductListViewModel", "Load products failed", throwable);
                                    products.setValue(new ArrayList<>());
                                }
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();  // Hủy subscription khi ViewModel bị hủy
    }
}
