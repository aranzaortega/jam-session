package com.aios.jamsession.retrofit;

import com.aios.jamsession.models.FCMBody;
import com.aios.jamsession.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:AAAAkoQ3nYg:APA91bHVx6mqCa_N7ObG2jmlHS8iyYHDSzWpx8zW8-waRcI6LRwaWbws3JV65-3JJzG6qyi4okaAHpxikbwQR817KdvPE3XLvgI3KLCMu2wj49aR91mGgPmYmZfH2TEjF5GxzoqxUWHu"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
