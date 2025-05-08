package com.example.beautyapp.retrofit;

import com.example.beautyapp.model.BaiVietModel;
import com.example.beautyapp.model.MessageModel;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
    @GET("adduser.php")
    Observable<MessageModel> addUser(
            @Query("user_id") String user_id,
            @Query("email") String email,
            @Query("pass") String pass,
            @Query("name") String name,
            @Query("birth") String birth
    );

    @GET("allarticle.php")
    Observable<BaiVietModel> getAllArticle(
            @Query("user_id") String user_id
    );
}
