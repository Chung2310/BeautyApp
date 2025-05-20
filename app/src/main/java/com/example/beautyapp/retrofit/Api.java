package com.example.beautyapp.retrofit;

import com.example.beautyapp.model.BaiVietModel;
import com.example.beautyapp.model.CartModel;
import com.example.beautyapp.model.ConsultantModel;
import com.example.beautyapp.model.ImageModel;
import com.example.beautyapp.model.MessageModel;
import com.example.beautyapp.model.ProductModel;
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
            @Query("userId") String userId,
            @Query("email") String email,
            @Query("pass") String pass,
            @Query("name") String name,
            @Query("birth") String birth
    );

    @GET("addbooking.php")
    Observable<MessageModel> addBooking(
            @Query("userId") String userId,
            @Query("phone") String phone,
            @Query("address") String address,
            @Query("date") String date,
            @Query("timeStart") String timeStart,
            @Query("timeEnd") String timeEnd,
            @Query("consultantId") int consultantId
    );

    @GET("allarticle.php")
    Observable<BaiVietModel> getAllArticle(
    );

    @GET("allproducts.php")
    Observable<ProductModel> getAllProducts(
    );

    @GET("allconsultant.php")
    Observable<ConsultantModel> getAllConsultant(
    );

    @GET("setLike.php")
    Observable<MessageModel> setLike(
            @Query("id") int id,
            @Query("userId") String userId
    );

    @GET("checkLike.php")
    Observable<MessageModel> checkLike(
            @Query("articleId") int articleId,
            @Query("userId") String userId
    );

    @GET("allarticleuser.php")
    Observable<BaiVietModel> getAllArticleUser(
            @Query("userId") String userId,
            @Query("page") int page
    );

    @GET("addarticle.php")
    Observable<MessageModel> addArticle(
            @Query("userId") String userId,
            @Query("time") String time,
            @Query("content") String content,
            @Query("numberLike") int numberLike,
            @Query("linkImage") String linkImage
            );

    @GET("addcart.php")
    Observable<MessageModel> addCart(
            @Query("userId") String userId,
            @Query("quantity") int quantity,
            @Query("productId") int productId
    );

    @GET("getuser.php")
    Observable<UserModel> getUser(
            @Query("userId") String userId
    );

    @GET("getcart.php")
    Observable<CartModel> getCart(
            @Query("userId") String userId
    );

    @GET("updateuser.php")
    Observable<UserModel> updateUser(
            @Query("userId") String userId,
            @Query("email") String email,
            @Query("name") String name,
            @Query("birth") String birth,
            @Query("imgae") String image
    );


    @Multipart
    @POST("uploadavt.php")
    Call<ImageModel> uploadFileAvt(@Part MultipartBody.Part file, @Query("userId") String userId);

    @Multipart
    @POST("uploadimagearticle.php")
    Call<ImageModel> uploadFileImage(@Part MultipartBody.Part file);
}
