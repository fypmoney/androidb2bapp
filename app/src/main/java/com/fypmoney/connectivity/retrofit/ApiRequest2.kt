package com.fypmoney.connectivity.retrofit


/**
 * @description This class is responsible to hold the data of web api request and work like as POJO class.
 */
class ApiRequest2(
    val purpose: String,//Used to get the purpose
    val endpoint: String, //Used to store api endpoint
    val request_type: Int, //Used to store api request type (ApiUrl.GET, ApiUrl.POST, ApiUrl.PUT)
    val param: Any, //Used to store api request parameter
    val onResponse: WebApiCaller.OnWebApiResponse, //Used to store reference of WebApiCaller.OnWebApiResponse to return the response
    val isProgressBar: Boolean? = false
)
