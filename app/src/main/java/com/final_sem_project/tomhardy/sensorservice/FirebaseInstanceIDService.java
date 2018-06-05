package com.final_sem_project.tomhardy.sensorservice;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.lang.annotation.Target;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by TomHardy on 24-02-2018.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private final String TAG = "FirebaseInstncIDService";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "onTokenRefresh is called " + token);
//        registerToken(token);
    }

    private void registerToken(String token) {
        Log.d(TAG, "registerToken is called");

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.1.2/fcm/register.php")
                .post(body)
                .build();

        try {
            Response result = client.newCall(request).execute();
            Log.d(TAG, "result : status " + result.isSuccessful());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}