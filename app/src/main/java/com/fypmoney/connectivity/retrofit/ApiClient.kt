package com.fypmoney.connectivity.retrofit

import com.fypmoney.BuildConfig
import com.fypmoney.util.AppConstants.API_TIMEOUT_SECONDS
import com.github.simonpercic.oklog3.OkLogInterceptor
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
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
            builder.apply {
                if (BuildConfig.DEBUG) {
                    // ok logging use to see network call in browser
                    val okLogInterceptor = OkLogInterceptor.builder().build()
                    builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    //builder.addInterceptor(ChuckerInterceptor(PockketApplication.instance))
                    builder.addInterceptor(okLogInterceptor)
                }
            }
            val client = builder.build()
            val dns = DnsOverHttps.Builder().client(client)
                .url("https://1.1.1.1/dns-query".toHttpUrl())
                .url("https://dns.google/dns-query".toHttpUrl())
                .build()
            client.newBuilder().dns(dns).build()
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        }



        /**
         * @description Method is used to set the timeout time and return OkHttpClient.Builder.
         */
        private fun getBuilder(): OkHttpClient.Builder {
            return OkHttpClient.Builder()
                .connectTimeout(API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        }


    }
}