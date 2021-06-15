package com.fypmoney.connectivity.retrofit

import com.fypmoney.connectivity.ApiConstant.BASE_URL
import com.fypmoney.util.AppConstants.API_TIMEOUT_SECONDS
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @description This class is responsible to setup client with retrofit.
 */
class ApiClient {

    companion object {
        /**
         * @param headers HashMap<String></String>, String>;
         * @param baseUrl String
         * @description Method is used to get instance of Retrofit after client setup .
         */
        fun getClient(baseUrl:String): Retrofit {
            val builder = getBuilder()
            builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(getFactory())
                .build()
        }



        /**
         * @description Method is used to set the timeout time and return OkHttpClient.Builder.
         */
        private fun getBuilder(): OkHttpClient.Builder {
            return OkHttpClient().newBuilder()
                .connectTimeout(API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        }

        /**
         * @description Method is used to return theGsonConverterFactory.
         */
        private fun getFactory(): GsonConverterFactory {
            return GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            )
        }
    }
}