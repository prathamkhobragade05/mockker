package com.pratham.mockker2;

import static com.pratham.mockker2.Splash.appDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.mockker2.database.AllEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class Home extends BaseActivity2 {
    static RecyclerView recyclerView;
    static ProgressBar progressBar;
    static List<ModelQuestion> modelQuestionList;
    static List<ModelTest> testList;
    static List<ModelTestTopic> topicList;

    QuestionBank questionBank;

    ImageView menu,refresh;
    TestAdapter Adapter;
    boolean homeTest=false;
    boolean testTopic=false;
    boolean topicSet=false;

    static ModelTest MODELTEST;
    static ModelTestTopic MODELTESTTOPIC;
    TextView path;
    String homePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        menu=findViewById(R.id.menu);
        refresh=findViewById(R.id.refresh);
        path=findViewById(R.id.path);
        progressBar=findViewById(R.id.progressBar);
        recyclerView=findViewById(R.id.tests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this,Results.class));
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Home.this, "notification", Toast.LENGTH_SHORT).show();
            }
        });
        getTests();
    }
    private void getTests() {                   //-------------tests
        progressBar.setVisibility(View.VISIBLE);
        homeTest=true;
        testList=new ArrayList<>();
        Executors.newSingleThreadExecutor().execute(()->{
            List<AllEntity.TestEntity> testEntityList= appDatabase.testDao().getAllTests();
            for(AllEntity.TestEntity test: testEntityList){
                ModelTest newTest= new ModelTest();
                newTest.setId(test.testId);
                newTest.setTest(test.testName);
                testList.add(newTest);
            }
            runOnUiThread(()->{
                TestAdapter testAdapter=new TestAdapter(testList,this,test->getTopics(test));
                recyclerView.setAdapter(testAdapter);
                progressBar.setVisibility(View.GONE);
                homePath="Home\\"+testList.size();
                path.setText(homePath);
            });
        });
    }

    void getTopics(ModelTest test) {
        MODELTEST = test;
        if (MODELTEST.getId() == 0) {                                    //------------Aptitude+Reasoning Questions Sets
            questionBank = new QuestionBank(Home.this, "aptitude_reasoning");

            File dir=questionBank.getDirectory();
            getTopicSets("aptitude_reasoning",dir);
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            testTopic = true;
            topicList = new ArrayList<>();
            Executors.newSingleThreadExecutor().execute(() -> {
                List<AllEntity.TopicEntity> topicEntityList = appDatabase.topicDao().getTopicsByTestId(test.getId());
                for (AllEntity.TopicEntity topic : topicEntityList) {
                    boolean isSufficientQuestions = appDatabase.questionDao().getQuestionCountByTopicId(topic.topicId) >= 10;
                    if (isSufficientQuestions) {
                        ModelTestTopic newTopic = new ModelTestTopic();
                        newTopic.setId(topic.topicId);
                        newTopic.setTest(topic.testId);
                        newTopic.setTopic(topic.topicName);
                        topicList.add(newTopic);
                    }
                }
                runOnUiThread(() -> {
                    TestTopicAdapter testTopicAdapter = new TestTopicAdapter(topicList, this, topicName -> topicQuestionCounter(topicName));
                    recyclerView.setAdapter(testTopicAdapter);
                    progressBar.setVisibility(View.GONE);
                    homePath = "Home\\" + test.getTest() + "\\" + topicList.size();
                    path.setText(homePath);
                });
            });
        }
    }

    private void topicQuestionCounter(String topicName){
        progressBar.setVisibility(View.VISIBLE);

        questionBank=new QuestionBank(Home.this,topicName);
        File dir=questionBank.getDirectory();

        List<ModelQuestion> topicAllQuestions=new ArrayList<>();
        Executors.newSingleThreadExecutor().execute(()->{
            List<AllEntity.QuestionEntity> questionEntityList=appDatabase.questionDao().getQuestionsByTopicId(MODELTESTTOPIC.getId());
            for(AllEntity.QuestionEntity question:questionEntityList){
//                if(question.topicId!= topic.id) continue;
                ModelQuestion newQuestion=new ModelQuestion();
                newQuestion.id=question.questionId;
                newQuestion.test=question.testId;
                newQuestion.topic=question.topicId;
                newQuestion.direction=question.direction;
                newQuestion.question=question.question;
                newQuestion.options=question.options;
                newQuestion.answer=question.answer;
                topicAllQuestions.add(newQuestion);
            }
            int queCount=topicAllQuestions.size();
            int counter=appDatabase.topicDao().getTopicQueCount(MODELTESTTOPIC.getId());
            if(counter==0 || counter!=queCount){
                Log.e("Home","generate new quesitons----- "+queCount+" "+counter);
                appDatabase.topicDao().updateQueCount(queCount,MODELTESTTOPIC.getId());
                questionBank.generateQuestionSets(topicAllQuestions,dir);
                getTopicSets(topicName,dir);
            }else{
                getTopicSets(topicName,dir);
            }
        });
    }
    void getTopicSets(String directoryFolder,File dir){
        topicSet=true;
        List<String> setList=questionBank.getTopicSets(dir,new ArrayList<>());
        runOnUiThread(()->{
            progressBar.setVisibility(View.GONE);
            if(setList.isEmpty()){
                Toast.makeText(this, "No questions try other topics", Toast.LENGTH_SHORT).show();
                return;
            }
            path.setText(homePath);
            TestTopicSetAdapter testTopicSetAdapter=new TestTopicSetAdapter(setList,Home.this,set ->loadQuestion(dir,set) );
            recyclerView.setAdapter(testTopicSetAdapter);
            homePath="Home\\"+MODELTEST.getTest()+"\\"+directoryFolder+"\\"+setList.size();
            path.setText(homePath);
        });
    }

    private void loadQuestion(File dir,String setItem) {
        progressBar.setVisibility(View.VISIBLE);
        questionBank.loadQuestionSets(dir,setItem.toLowerCase()+".json");

        Intent intent=new Intent(Home.this, MainActivity.class);

        Long topicId;
        if(MODELTEST.getId()==0){
            topicId=0L;
        }else{
            topicId=MODELTESTTOPIC.getId();
        }
        intent.putExtra("topicid",topicId);
        intent.putExtra("set",setItem);
        progressBar.setVisibility(View.GONE);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(topicSet){
            topicSet=false;
            getTopics(MODELTEST);
        }
        else if(testTopic){
            testTopic=false;
            getTests();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        getTests();
        Log.e("Home","back to the home");
    }

    @Override
    protected void onPause(){
        super.onPause();
    }
}