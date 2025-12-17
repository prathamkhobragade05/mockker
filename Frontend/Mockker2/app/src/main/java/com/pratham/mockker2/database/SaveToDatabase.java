package com.pratham.mockker2.database;


import static com.pratham.mockker2.Splash.UserId;
import static com.pratham.mockker2.Splash.apiServer;
import static com.pratham.mockker2.Splash.appDatabase;
import static com.pratham.mockker2.Splash.isFetchedData;
import static com.pratham.mockker2.Splash.isResultUploaded;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.util.Log;

import com.pratham.mockker2.ModelQuestion;
import com.pratham.mockker2.ModelResult;
import com.pratham.mockker2.ModelTestTopic;
import com.pratham.mockker2.QuestionBank;
import com.pratham.mockker2.Splash;
import com.pratham.mockker2.database.AllEntity.*;
import com.pratham.mockker2.ModelAllTalbes;
import com.pratham.mockker2.ModelTest;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveToDatabase {
    ModelAllTalbes modelAllTalbes;
    Context context;

    List<ModelTest> testList=new ArrayList<>();
    public SaveToDatabase(Context context, ModelAllTalbes modelAllTalbes) {
        this.modelAllTalbes=modelAllTalbes;
        this.context=context;

        Log.e("SaveToDatabse---","saving to database.........");
        saveTests();
        saveTopics();
        saveQuestions();
        saveResults();
    }
    public static void saveUserId(Long loginId,String userName){
        UserEntity user=new UserEntity();
        user.userId=loginId;
        user.userName=userName;
        Log.e("save to Database 56", loginId+" ---- "+userName);
        appDatabase.userDao().insertUser(user);
    }

    void saveTests(){                          //----------tests
        List<TestEntity> testEntityList= new ArrayList<>();
        List<Long> updatedTestId=new ArrayList<>();
        for(ModelTest test: modelAllTalbes.tests){
            TestEntity newTest=new TestEntity();
            newTest.testId=test.getId();        updatedTestId.add(test.getId());
            newTest.testName=test.getTest();
            testEntityList.add(newTest);
            testList.add(test);
        }
        try{
            appDatabase.testDao().insertAllTests(testEntityList);
            appDatabase.testDao().deleteExceptTestId(updatedTestId);
        }catch (Exception e){
            Log.e("test insertion", "Failed "+e.getMessage());
        }
    }

    void saveTopics(){                          //----------tests
        List<TopicEntity> topicEntityList= new ArrayList<>();
        List<Long> updatedTopics= new ArrayList<>();
        for(ModelTestTopic topic: modelAllTalbes.topics){
            TopicEntity newTopic=new TopicEntity();
            newTopic.topicId=topic.getId();
            newTopic.testId=topic.getTest();
            newTopic.topicName=topic.getTopic();
            newTopic.queCount=0;
//            newTopic.queCount=appDatabase.questionDao().getQuestionCountByTopicId(topic.getId());
            topicEntityList.add(newTopic);
            updatedTopics.add(topic.getId());
        }
        try{
            appDatabase.topicDao().insertAllTopics(topicEntityList);
            appDatabase.topicDao().deleteExceptTopicId(updatedTopics);
        }catch (Exception e){
            Log.e("topic" +"insertion", "Failed "+e.getMessage());
        }
    }
    void saveQuestions(){
        List<ModelQuestion> modelQuestionList=modelAllTalbes.questions;
        int oldQueCount=appDatabase.questionDao().getAllQuestionCount();

        List<QuestionEntity> questionEntityList= new ArrayList<>();
        List<Long> updatedId=new ArrayList<>();
        for(ModelQuestion question: modelQuestionList){
            QuestionEntity newQuestion=new QuestionEntity();
            newQuestion.questionId=question.getId();    updatedId.add(question.getId());

            newQuestion.testId=question.getTest();
            newQuestion.topicId=question.getTopic();
            newQuestion.direction=question.getDirection();
            newQuestion.question=question.getQuestion();
            newQuestion.options= question.getOptions();
            newQuestion.answer=question.getAnswer();
            questionEntityList.add(newQuestion);
        }
        try{
            appDatabase.questionDao().insertAllQuestions(questionEntityList);
            appDatabase.questionDao().deleteExceptQuestionId(updatedId);

            int updatedQueCount=modelQuestionList.size();

            if(oldQueCount!=updatedQueCount){
                try{
                    generateAptitudeReasoningQues(modelQuestionList);
                    generateTestAllQues(modelQuestionList);
                }catch (Exception e){
                }
            }
        }catch (Exception e){
            Log.e("question insertion", "Failed "+e.getMessage());
        }
    }

    void generateTestAllQues(List<ModelQuestion> modelQuestionList){
        Map<Long,List<ModelQuestion>> testWiseQuestions;
        testWiseQuestions=new LinkedHashMap<>();
        for(ModelQuestion question:modelQuestionList){
            Long testId=question.getTest();
            testWiseQuestions.putIfAbsent(testId,new ArrayList<>());
            testWiseQuestions.get(testId).add(question);
        }

        for(ModelTest test:testList){
            Long testId=test.getId();
            String testName=test.getTest();

            List<ModelQuestion> testIdQuestions=new ArrayList<>();
            testIdQuestions=testWiseQuestions.get(testId);

            if(testIdQuestions!=null){
                QuestionBank questionBank=new QuestionBank(context,testName+"_mix");
                File dir=questionBank.getDirectory();
                try{
                    questionBank.generateQuestionSets(testIdQuestions,dir);
                }catch (Exception e){
                    Log.e("saveToDatabase 156","exception in generatingsets for "+testName+"_mix questions:"+testIdQuestions.size()+"  -- "+e.getMessage());
                }
            }
        }
    }

    void generateAptitudeReasoningQues(List<ModelQuestion> modelQuestionList){
        QuestionBank questionBank=new QuestionBank(context,"aptitude_reasoning");
        File dir=questionBank.getDirectory();
        Collections.shuffle(modelQuestionList);
        questionBank.generateQuestionSets(modelQuestionList,dir);
    }

    void saveResults(){                         //-----------results
        List<ResultEntity> resultEntityList =new ArrayList<>();
        List<String> updatedDate=new ArrayList<>();
        int id=1;
        for (ModelResult result: modelAllTalbes.results){
            ResultEntity newResult=new ResultEntity();
            if(!UserId.equals(result.getUserId())) continue;

            newResult.resultId=id;
            newResult.userId=result.getUserId();
            newResult.testId=result.getTestId();
            newResult.topicId=result.getTopicId();
            newResult.score=result.getScore();
            newResult.date=result.getDateTime();    updatedDate.add(result.getDateTime());
            resultEntityList.add(newResult);
            id++;
        }
        try{
            appDatabase.runInTransaction(()->{
                if(isResultUploaded && isFetchedData){
                    appDatabase.resultDao().replaceAll(resultEntityList);
                }
            });
        }catch (Exception e){
            Log.e("result insertion", "Failed "+e.getMessage());
        }
    }
}
