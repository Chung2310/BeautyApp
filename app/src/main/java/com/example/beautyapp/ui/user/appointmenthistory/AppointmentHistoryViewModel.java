package com.example.beautyapp.ui.user.appointmenthistory;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beautyapp.model.Booking;
import com.example.beautyapp.retrofit.Api;
import com.example.beautyapp.retrofit.RetrofitClient;
import com.example.beautyapp.utils.Utils;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AppointmentHistoryViewModel extends ViewModel {
    private MutableLiveData<List<Booking>> appointmentList = new MutableLiveData<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Api api;

    public AppointmentHistoryViewModel() {
        api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
    }

    public LiveData<List<Booking>> getAppointmentList() {
        return appointmentList;
    }

    public void fetchAppointmentList(String userId) {
        compositeDisposable.add(api.getBooking(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        bookingModel -> {
                            if (bookingModel.isSuccess()) {
                                appointmentList.setValue(bookingModel.getResult());
                            }
                        },
                        throwable -> Log.e("AppointmentVM", throwable.getMessage())
                ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}