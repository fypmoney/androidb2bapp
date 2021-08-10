package com.fypmoney.connectivity.retrofit

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.fypmoney.BuildConfig
import com.fypmoney.application.PockketApplication
import com.fypmoney.connectivity.retrofit.ApiClient1.context
import com.fypmoney.util.AppConstants.API_TIMEOUT_SECONDS
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.apache.http.conn.ssl.SSLSocketFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Array.get
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

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
            if (BuildConfig.DEBUG) {
                // Added interceptor for http logging
                builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                builder.addInterceptor(ChuckerInterceptor(PockketApplication.instance))
            }
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
            return OkHttpClient.Builder()
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

        fun getUnsafeOkHttpClient(): OkHttpClient? {
            return try {
                // Create a trust manager that does not validate certificate chains
                val trustAllCerts: Array<TrustManager> =
                    arrayOf<TrustManager>(object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: String?
                        ) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(
                            chain: Array<X509Certificate?>?,
                            authType: String?
                        ) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate?> {
                            return arrayOfNulls(0)
                        }
                    } )


                // Install the all-trusting trust manager
                val sslContext: SSLContext = SSLContext.getInstance("TLS")
                sslContext.init(
                    null, trustAllCerts,
                    SecureRandom()
                )
                // Create an ssl socket factory with our all-trusting manager
                val sslSocketFactory: javax.net.ssl.SSLSocketFactory? = sslContext
                    .socketFactory
                var okHttpClient = OkHttpClient()
                okHttpClient = okHttpClient.newBuilder().addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .hostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build()
                okHttpClient
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }
}