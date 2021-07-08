package com.example.shopping;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface api {

@Multipart
    @POST("/api/order/fee")
Call<ResponsTest> uploadphoto(@Part MultipartBody.Part image,
                                    @Part ("orderId") RequestBody orderId,
                                    @Part ("carId") RequestBody carId,
                                    @Part ("price") RequestBody price,
                                    @Part ("note") RequestBody note
                              );


}
