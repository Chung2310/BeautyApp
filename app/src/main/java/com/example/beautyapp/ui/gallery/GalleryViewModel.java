package com.example.beautyapp.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beautyapp.model.Product;
import com.example.beautyapp.model.ProductModel;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GalleryViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> productList = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final Api api;

    public GalleryViewModel() {
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
        loadProducts();
    }

    void loadProducts() {
        compositeDisposable.add(api.getAllProducts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        productModel -> {
                            List<Product> products = (List<Product>) productModel.getResult();
                            productList.setValue(products);
                        },
                        throwable -> {
                            // Optional: xử lý lỗi, ví dụ:
                            throwable.printStackTrace();
                        }
                ));
    }

    public LiveData<List<Product>> getProductList() {
        return productList;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear(); // tránh memory leak
    }
}
