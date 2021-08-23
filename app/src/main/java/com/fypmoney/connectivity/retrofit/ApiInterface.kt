package com.fypmoney.connectivity.retrofit

import com.fypmoney.BuildConfig
import com.fypmoney.util.AppConstants.APP_VERSION
import com.fypmoney.util.AppConstants.CLIENT_TYPE
import com.fypmoney.util.AppConstants.CLIENT_TYPE_VALUE
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

/**
 * @description It contains WebApi methods.
 */
interface ApiInterface {

    /**
     * @param endPoint String
     * @return return ResponseBody
     * @description Call GET type's web API
     */
    @Headers("Accept: application/json")
    @GET
    fun getDataFromServer(
        @Header("client_id") client_id: String?,
        @Header("Authorization") authorization: String?,
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Url endPoint: String
    ): Observable<ResponseBody>


    /**
     * @param endPoint String
     * @return return ResponseBody
     * @description Call GET type's web API
     */
    @Headers("Accept: application/json")
    @GET
    fun getDataFromServer1(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Header("Authorization") authorization: String?,
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Url endPoint: String
    ): Observable<ResponseBody>


    /**
     * @param endPoint String
     * @param hashMap  HashMap<String,Object>
     * @return return response
     * @description Call POST type's web API
     */
    @Headers("Accept: application/json")
    @POST
    fun postAuthDataOnServer(
        @Header("client_id") client_id: String?,
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Header(APP_VERSION) app_version: Int = BuildConfig.VERSION_CODE,
        @Header("client_secret") client_secret: String?,
        @Header("grant_type") grant_type: String?, @Url endPoint: String, @Body request: Any
    ): Observable<ResponseBody>

    /**
     * @param endPoint String
     * @param hashMap  HashMap<String,Object>
     * @return return response
     * @description Call POST type's web API
     */
    @Multipart
    @POST
    fun postImageOnServer(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Header("Authorization") authorization: String?,
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Header(APP_VERSION) app_version: Int = BuildConfig.VERSION_CODE,
        @Url endPoint: String, @Part file: MultipartBody.Part? = null
    ): Observable<ResponseBody>

    @Headers("Accept: application/json")
    @POST
    fun postLoginInitDataOnServer(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Header("Authorization") authorization: String?,
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Header(APP_VERSION) app_version: Int = BuildConfig.VERSION_CODE,
        @Header("one-tap") one_tap: Boolean?, @Url endPoint: String, @Body request: Any
    ): Observable<ResponseBody>

    @Headers("Accept: application/json")
    @POST
    fun getPaginationApiCalling(
        @Url endPoint: String,
        @Header("client_id") client_id: String?,
        @Header("Authorization") authorization: String?,
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Header(APP_VERSION) app_version: Int = BuildConfig.VERSION_CODE,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
        @Body request: Any
    ): Observable<ResponseBody>

    @Headers("Accept: application/json")
    @POST
    fun postLoginDataOnServer(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Header(APP_VERSION) app_version: Int = BuildConfig.VERSION_CODE,
        @Header("one-tap") one_tap: Boolean?, @Url endPoint: String, @Body request: Any
    ): Observable<ResponseBody>

    @Headers("Accept: application/json")
    @POST
    fun postDataOnServer(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Header(APP_VERSION) app_version: Int = BuildConfig.VERSION_CODE,
        @Header("Authorization") authorization: String?, @Url endPoint: String, @Body request: Any
    ): Observable<ResponseBody>


    /**
     * @param endPoint String
     * @param hashMap  HashMap<String,Object>
     * @return return response
     * @description Call POST type's web API
     */
    @FormUrlEncoded
    @Headers("Accept: application/x-www-form-urlencoded")
    @POST
    fun postDataOnPayUServer(
        @Url endPoint: String,
        @Field("command") command: String,
        @Field("var1") var1: String,
        @Field("key") key: String,
        @Field("hash") hash: String
    ): Observable<ResponseBody>

    /**
     * @param endPoint String
     * @param hashMap  HashMap<String,Object>
     * @return return response
     * @description Call PUT type's web API
     */
    @PUT
    fun putDataOnServer(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Header(APP_VERSION) app_version: Int = BuildConfig.VERSION_CODE,
        @Header("Authorization") authorization: String?, @Url endPoint: String
    ): Observable<ResponseBody>

    @PUT
    fun putDataOnServerWithBody(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Body request: Any,
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Header(APP_VERSION) app_version: Int = BuildConfig.VERSION_CODE,
        @Header("Authorization") authorization: String?, @Url endPoint: String
    ): Observable<ResponseBody>

    @PUT
    fun putDataOnServerWithBodyWithoutAppId(
        @Header("client_id") client_id: String?,
        @Body request: Any,
        @Header("Authorization") authorization: String?, @Url endPoint: String
    ): Observable<ResponseBody>

    @DELETE
    fun deleteDataOnServer(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Header(APP_VERSION) app_version: Int = BuildConfig.VERSION_CODE,
        @Header("Authorization") authorization: String?, @Url endPoint: String
    ): Observable<ResponseBody>

    @Headers("Accept: application/json")
    @GET
    fun checkAppUpdate(
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Header(APP_VERSION) app_version: Int = BuildConfig.VERSION_CODE,
        @Header("Content-Type") content_type: String = "application/json",
        @Url endPoint: String
    ): Observable<ResponseBody>

    @Headers("Accept: application/json")
    @GET
    fun getTopTenUsers(
        @Header(CLIENT_TYPE) client_type: String = CLIENT_TYPE_VALUE,
        @Header("Content-Type") content_type: String = "application/json",
        @Header(APP_VERSION) app_version: Int = BuildConfig.VERSION_CODE,
        @Url endPoint: String
    ): Observable<ResponseBody>


}
