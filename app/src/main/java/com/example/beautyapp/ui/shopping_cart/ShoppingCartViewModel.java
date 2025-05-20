package com.example.beautyapp.ui.shopping_cart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beautyapp.model.Cart;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ShoppingCartViewModel extends ViewModel {

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<List<Cart>> cartItems = new MutableLiveData<>();
    private final Api api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public LiveData<List<Cart>> getCartItems() {
        return cartItems;
    }

    public void loadCartItems() {
        compositeDisposable.add(api.getCart(firebaseAuth.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        cartModel -> cartItems.setValue(cartModel.getResult()),
                        throwable -> cartItems.setValue(new ArrayList<>())
                ));
    }

    public void updateQuantity(int productId, int quantity) {
        // TODO: Gọi API cập nhật nếu cần
    }

    public void deleteCartItem(int productId) {
        List<Cart> currentList = cartItems.getValue();
        if (currentList != null) {
            List<Cart> updatedList = new ArrayList<>(currentList);
            updatedList.removeIf(cart -> cart.getProductId() == productId);
            cartItems.setValue(updatedList);
        }

        // TODO: Gọi API xóa nếu cần
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
