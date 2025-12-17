package com.pratham.mockker2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pratham.mockker2.database.AllEntity;
import com.pratham.mockker2.database.AppDatabase;
import com.pratham.mockker2.database.SaveToDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {
    public static ApiServer apiServer;
    public static AppDatabase appDatabase;
    public static Long UserId;
    public static boolean isResultUploaded=false;
    public static boolean isFetchedData=false;

    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        appDatabase= AppDatabase.getInstance(getApplicationContext());
        apiServer=ApiClient.getClient().create(ApiServer.class);
        progressBar=findViewById(R.id.progressBar);

        Executors.newSingleThreadExecutor().execute(()->{
            UserId=appDatabase.userDao().getLoginId();
            if(UserId!=null){
                runOnUiThread(()->{
                    progressBar.setVisibility(View.VISIBLE);
                });
                getResultFromRoom();
            }else{
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    startActivity(new Intent(Splash.this,Login.class));
                    finish();
                }, 2000); // 2000  = 2 seconds
            }
        });
    }

    private void getResultFromRoom(){
        int id=0;
        String New=UserId.toString()+id;
        Long resultId= Long.valueOf(New);
        List<ModelResult> resultList=new ArrayList<>();
        List<AllEntity.ResultEntity> resultEntityList= appDatabase.resultDao().getAllResults();
        for(AllEntity.ResultEntity result: resultEntityList){
            ModelResult newResult=new ModelResult();
            if(UserId!=result.userId) continue;
            newResult.setId(resultId);
            newResult.setUserId(result.userId);
            newResult.setTestId(result.testId);
            newResult.setTopicId(result.topicId);
            newResult.setScore(result.score);
            newResult.setDateTime(result.date);
            resultList.add(newResult);
            Log.e("Splash--- ","getId: "+result.resultId+"\t\tsetId:"+resultId);
            resultId++;
        }
        if(!resultList.isEmpty())
            saveResultsToServer(resultList);
        else
            setLocalDatabase();
    }

    private void saveResultsToServer(List<ModelResult> modelResultList){
        Call<Void> saveResultCall=apiServer.saveResults(modelResultList);
        saveResultCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(Splash.this, "Server Error", Toast.LENGTH_SHORT).show();
                    Log.e("Splass server eroor result upload","server Erro");
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        finish();
                    }, 2000);
                }
                else
                    isResultUploaded=true;
                setLocalDatabase();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                Log.e("Splash saveResultToServer","Exception but setLocalDatabase " +throwable.getMessage());
                Toast.makeText(Splash.this, "Exception but setLocalDatabase", Toast.LENGTH_SHORT).show();
                setLocalDatabase();
            }
        });
    }

    void setLocalDatabase(){
        Call<ModelAllTalbes> getAllTablesCall=apiServer.getAllTables(UserId);
        getAllTablesCall.enqueue(new Callback<ModelAllTalbes>() {
            @Override
            public void onResponse(Call<ModelAllTalbes> call, Response<ModelAllTalbes> response) {
                if(response.isSuccessful()){
                    isFetchedData=true;
                    Executors.newSingleThreadExecutor().execute(()->{
                        new SaveToDatabase(Splash.this,response.body());
                        runOnUiThread(()->{
                            progressBar.setVisibility(View.GONE);
                        });
                    });

                }else{
                    Log.e("Login setLocalDAtabase","failed");
                }
                startActivity(new Intent(Splash.this,Home.class));
                finish();
            }

            @Override
            public void onFailure(Call<ModelAllTalbes> call, Throwable throwable) {
                Log.e("setLocalDatabase","not connected!");
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    progressBar.setVisibility(View.GONE);
                    Intent intent =new Intent(Splash.this,Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }, 2000);
            }
        });
    }


}