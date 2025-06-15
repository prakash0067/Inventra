package com.example.inventra.DBConnect;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_IMAGE_URL = "http://10.0.2.2/Inventra/img/";
    public static final String BASE_PROFILE_URL = "http://10.0.2.2/Inventra/profile/";
    private static final String BASE_URL = "http://10.0.2.2/Inventra/";
//    public static final String BASE_IMAGE_URL = "http://192.168.1.2/Inventra/img/";
//    private static final String BASE_URL = "http://192.168.1.2/Inventra/";
//    public static final String BASE_PROFILE_URL = "http://192.168.1.2/Inventra/profile/";


    public static Retrofit getRetrofitInstance() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
