package edu.uncc.project10;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/api/login")
    Call<LoginResult> login(@Body HashMap<String, String> data);

    @POST("/api/signup")
    Call<SignupResult> signup(@Body HashMap<String, String> data);

    @POST("/api/user/update")
    Call<UpdateUserResult> updateUser(@Header ("x-jwt-token") String token, @Body HashMap<String, Object> data);
}
