package com.fypmoney.connectivity.retrofit

import io.reactivex.Observable
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
        @Header("client_secret") client_secret: String?,
        @Header("grant_type") grant_type: String?, @Url endPoint: String, @Body request: Any
    ): Observable<ResponseBody>

    @Headers("Accept: application/json")
    @POST
    fun postLoginInitDataOnServer(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Header("Authorization") authorization: String?,
        @Header("one-tap") one_tap: Boolean?, @Url endPoint: String, @Body request: Any
    ): Observable<ResponseBody>


    @Headers("Accept: application/json")
    @POST
    fun postLoginDataOnServer(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Header("one-tap") one_tap: Boolean?, @Url endPoint: String, @Body request: Any
    ): Observable<ResponseBody>

    @Headers("Accept: application/json")
    @POST
    fun postDataOnServer(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Header("Authorization") authorization: String?, @Url endPoint: String, @Body request: Any
    ): Observable<ResponseBody>


    /**
     * @param endPoint String
     * @param hashMap  HashMap<String,Object>
     * @return return response
     * @description Call POST type's web API
     */
    @POST
    fun postDataOnServer1(@Url endPoint: String, @Body request: Any): Observable<ResponseBody>


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
        @Header("Authorization") authorization: String?, @Url endPoint: String
    ): Observable<ResponseBody>

    @PUT
    fun putDataOnServerWithBody(
        @Header("client_id") client_id: String?,
        @Header("appId") appId: String?,
        @Body request: Any,
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
        @Header("Authorization") authorization: String?, @Url endPoint: String
    ): Observable<ResponseBody>


    @Headers("Accept: application/json")
    @GET
    fun getYourTasks(
        @Header("client_id") client_id: String?,
        @Header("Authorization") authorization: String?,
        @Url endPoint: String
    ): Observable<ResponseBody>

}
