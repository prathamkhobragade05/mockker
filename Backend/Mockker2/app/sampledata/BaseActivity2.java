package com.pratham.mockker2;

import static com.pratham.mockker2.Splash.UserId;
import static com.pratham.mockker2.Splash.apiServer;
import static com.pratham.mockker2.Splash.appDatabase;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.pratham.mockker2.database.AllEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity2 extends AppCompatActivity {

    Snackbar noInternetSnackbar;
    ConnectivityManager.NetworkCallback networkCallback;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                if(!isRESULT_SAVED){
                    getResults();
                    runOnUiThread(() -> {
                        Snackbar.make(findViewById(android.R.id.content),"uploading results",Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(getResources().getColor(android.R.color.holo_green_light))
                                .setTextColor(Color.WHITE).show();
                    });
                }
            }
        };
    }
    private void getResults(){                                                  //-----------get logged in user results from room database
        List<ModelResult> resultList=new ArrayList<>();
        Executors.newSingleThreadExecutor().execute(()->{
            List<AllEntity.ResultEntity> resultEntityList= appDatabase.resultDao().getAllResults();

            for(AllEntity.ResultEntity result: resultEntityList){
                ModelResult newResult=new ModelResult();
                if(UserId!=result.userId) continue;
//                newResult.setId(result.resultId);
                newResult.setUserId(result.userId);
                newResult.setTestId(result.testId);
                newResult.setTopicId(result.topicId);
                newResult.setScore(result.score);
                newResult.setDateTime(result.date);
                resultList.add(newResult);
            }
            if(!resultList.isEmpty()){
                saveResultsToServer(resultList);
            }else{
                isRESULT_SAVED=true;
            }

        });
    }
    public static boolean isRESULT_SAVED;
    private void saveResultsToServer(List<ModelResult> modelResultList){
        Call<String> saveResultCall=apiServer.saveResults(modelResultList);
        saveResultCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    isRESULT_SAVED=true;
                    Log.e("BaseAci2","result saved=========");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.e("BaseActivity2 saveResultToServer","error " +throwable.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest request = new NetworkRequest.Builder().build();
        connectivityManager.registerNetworkCallback(request, networkCallback);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        } catch (Exception ignored) {}
    }
}
