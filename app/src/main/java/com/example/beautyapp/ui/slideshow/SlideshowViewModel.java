    package com.example.beautyapp.ui.slideshow;
    
    import android.util.Log;

    import androidx.lifecycle.LiveData;
    import androidx.lifecycle.MutableLiveData;
    import androidx.lifecycle.ViewModel;
    
    import com.example.beautyapp.model.Consultant;
    import com.example.beautyapp.model.Product;
    import com.example.beautyapp.retrofit.Api;
    import com.example.beautyapp.retrofit.RetrofitClient;
    import com.example.beautyapp.utils.Utils;
    
    import java.util.List;
    
    import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
    import io.reactivex.rxjava3.disposables.CompositeDisposable;
    import io.reactivex.rxjava3.schedulers.Schedulers;
    
    public class SlideshowViewModel extends ViewModel {
    
        private final MutableLiveData<List<Consultant>> consultantlist = new MutableLiveData<>();
        private final CompositeDisposable compositeDisposable = new CompositeDisposable();
        private final Api api;
    
        public SlideshowViewModel() {
            api = RetrofitClient.getInstance(Utils.BASE_URL).create(Api.class);
            loadConsultant();
        }
    
        private void loadConsultant() {
            compositeDisposable.add(api.getAllConsultant()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            consultantModel -> {
                                List<Consultant> consultants =  consultantModel.getResult();
                                Log.d("slideshow",consultants.get(0).getImageUrl());
                                consultantlist.setValue(consultants);
                            },
                            throwable -> {
                                throwable.printStackTrace();
                            }
                    ));
        }
        public LiveData<List<Consultant>> getConsultantList() {
            return consultantlist;
        }
    
        @Override
        protected void onCleared() {
            super.onCleared();
            compositeDisposable.clear();
        }
    }