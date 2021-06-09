package com.aios.jamsession.providers;

import com.aios.jamsession.models.FCMBody;
import com.aios.jamsession.models.FCMResponse;
import com.aios.jamsession.retrofit.IFCMApi;
import com.aios.jamsession.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider(){}

    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
