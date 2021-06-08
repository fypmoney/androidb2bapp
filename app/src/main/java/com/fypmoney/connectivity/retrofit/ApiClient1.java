package com.fypmoney.connectivity.retrofit;


import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient1 {
    static Context context;
    public ApiClient1(Context context)
    {
        ApiClient1.context = context;
    }

    public static Retrofit getClient() {
        return new Retrofit.Builder()
                .baseUrl("http://54.84.197.170:9300/services/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
    }
