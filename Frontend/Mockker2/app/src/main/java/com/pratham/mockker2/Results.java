package com.pratham.mockker2;

import static com.pratham.mockker2.Splash.UserId;
import static com.pratham.mockker2.Splash.appDatabase;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratham.mockker2.database.AllEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Results extends AppCompatActivity {
    ApiServer apiServer;

    RecyclerView resultView;
    TextView nullResult,userName;
    ResultsAdapter resultsAdapter;
    Button Logout;

    List<ModelTest> testList;
    List<ModelTestTopic> topicList;
    List<ModelResult> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiServer=ApiClient.getClient().create(ApiServer.class);

        userName=findViewById(R.id.userName);
        nullResult=findViewById(R.id.nullResult);
        Logout=findViewById(R.id.logOut);
        resultView=findViewById(R.id.resultView);
        resultView.setLayoutManager(new LinearLayoutManager(this));
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
        Executors.newSingleThreadExecutor().execute(()->{
            String UserName=appDatabase.userDao().getUserName();
//            Log.e("Results 72",UserName);
            userName.setText(UserName);
            getResult();
            Log.e("Results","------getResults");
            getTests();
            Log.e("Results","------getTest");
            getTopics();
            Log.e("Results","------getTopic");
            runOnUiThread(()->{
                if(!resultList.isEmpty()) {
                    nullResult.setVisibility(View.GONE);
                    resultsAdapter = new ResultsAdapter(resultList,testList,topicList, Results.this);
                    resultView.setAdapter(resultsAdapter);
                }else{
                    nullResult.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void logOut(){
        Executors.newSingleThreadExecutor().execute(()->{
            appDatabase.runInTransaction(() -> {
//                appDatabase.resultDao().deleteAllResults();

            });
            appDatabase.userDao().deleteUserId();
            Intent intent=new Intent(Results.this,Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void getTests() {                   //-------------tests
        testList=new ArrayList<>();
//        Executors.newSingleThreadExecutor().execute(()->{
            List<AllEntity.TestEntity> testEntityList= appDatabase.testDao().getAllTests();
            for(AllEntity.TestEntity test: testEntityList){
                ModelTest newTest= new ModelTest();
                newTest.setId(test.testId);
                newTest.setTest(test.testName);
                testList.add(newTest);
            }
        Log.e("Results","------getTests()"+testList.size());
//        });
    }
    void getTopics(){
        topicList=new ArrayList<>();
//        Executors.newSingleThreadExecutor().execute(()->{
            List<AllEntity.TopicEntity> topicEntityList=appDatabase.topicDao().getAllTopics();
            for(AllEntity.TopicEntity topic:topicEntityList){
                ModelTestTopic newTopic=new ModelTestTopic();
                newTopic.setId(topic.topicId);
                newTopic.setTest(topic.testId);
                newTopic.setTopic(topic.topicName);
                topicList.add(newTopic);
            }
        Log.e("Results","------getTopic() "+topicList.size());
//        });
    }

    private void getResult(){
        resultList=new ArrayList<>();
        int resultCount=appDatabase.resultDao().getAllResultsCount();
        Log.e("Results ","Results count "+resultCount);

            List<AllEntity.ResultEntity> resultEntityList= appDatabase.resultDao().getAllResults();
            for(AllEntity.ResultEntity result: resultEntityList){
                ModelResult newResult=new ModelResult();
                if(UserId!=result.userId) continue;
                newResult.setId(result.resultId);
                newResult.setUserId(result.userId);
                newResult.setTestId(result.testId);
                newResult.setTopicId(result.topicId);
                newResult.setScore(result.score);
                newResult.setDateTime(result.date);
                resultList.add(newResult);
            }
        Log.e("Results","------getResults() size"+resultList.size());
    }
    private void getResults(){
        Call<List<ModelResult>> getResultsCall=apiServer.getResult(UserId);
        getResultsCall.enqueue(new Callback<List<ModelResult>>() {
            @Override
            public void onResponse(Call<List<ModelResult>> call, Response<List<ModelResult>> response) {
                List<ModelResult> resultList= response.body();
                resultsAdapter=new ResultsAdapter(resultList,testList,topicList, Results.this);
                resultView.setAdapter(resultsAdapter);
            }

            @Override
            public void onFailure(Call<List<ModelResult>> call, Throwable throwable) {
                Log.e("getAllTest error",throwable.getMessage());
            }
        });
    }
}