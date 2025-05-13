package com.example.beautyapp.retrofit;

import com.example.beautyapp.model.BaiVietModel;
import com.example.beautyapp.model.ImageModel;
import com.example.beautyapp.model.MessageModel;
import com.example.beautyapp.model.UserModel;
import com.google.type.DateTime;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
    );

    @GET("allarticle.php")
    Observable<BaiVietModel> getAllArticleUser(
            @Query("user_id") String user_id
    );

    @GET("addarticle.php")
    Observable<MessageModel> addArticle(
            @Query("user_id") String user_id,
            @Query("time") String time,
            @Query("content") String content,
            @Query("numberLike") int numberLike,
            @Query("linkImage") String linkImage
            );

    @GET("getuser.php")
    Observable<UserModel> getUser(
            @Query("user_id") String user_id
    );
    @GET("updateuser.php")
    Observable<UserModel> updateUser(
            @Query("user_id") String user_id,
            @Query("email") String email,
            @Query("name") String name,
            @Query("birth") String birth,
            @Query("imgae") String image
    );
    @Multipart
    @POST("uploadavt.php")
    Call<ImageModel> uploadFileAvt(@Part MultipartBody.Part file, @Query("user_id") String user_id);
}
