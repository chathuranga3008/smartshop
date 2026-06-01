package lk.exam.smartshop_admin.service;

import lk.exam.smartshop_admin.model.AuthResponce;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface SmartShopService {
    @FormUrlEncoded
    @POST("loginController.php")
    Call<AuthResponce> sendVcode(@Field("email") String email);

    @FormUrlEncoded
    @POST("verifyController.php")
    Call<AuthResponce> login(@Field("vcode") String vcode);
}
