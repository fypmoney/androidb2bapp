package com.fypmoney.connectivity.retrofit;

import com.fypmoney.model.addTaskModal.AddTaskRequest;
import com.fypmoney.model.addTaskModal.AddTaskResponce;
import com.fypmoney.model.chaoseHistory.ChoresHistoryResponse;
import com.fypmoney.model.yourTaskModal.TaskRequest;
import com.fypmoney.model.yourTaskModal.YourTaskResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface Allapi {


    @Headers("Accept: application/json")
    @POST("PockketService/internal/get/approval-request")
    Call<YourTaskResponse> getTask(@Header("appId") String appId, @Header("client_id") String client_id,
                                   @Header("Authorization") String Authorization, @Body() TaskRequest task);


    @Headers("Accept: application/json")
    @GET("loyaltyservice/api/taskMaster")
    Call<ChoresHistoryResponse> getChoresHistory(@Header("client_id") String client_id,
                                                 @Header("Authorization") String Authorization);

    @Headers("Accept: application/json")
    @POST("loyaltyservice/api/task")
    Call<AddTaskResponce> AddTask(@Header("client_id") String client_id,
                                  @Header("Authorization") String Authorization, @Body() AddTaskRequest task);


}
