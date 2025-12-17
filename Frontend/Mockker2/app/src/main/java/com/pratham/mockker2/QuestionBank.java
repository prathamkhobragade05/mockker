package com.pratham.mockker2;

import static com.pratham.mockker2.Home.modelQuestionList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pratham.mockker2.database.AllEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class QuestionBank {
    Context context;
    String  setDirectory;
    static TestTopicSetAdapter testTopicSetAdapter;

    public QuestionBank(Context context, String setDirectory){
        this.context=context;
        this.setDirectory=setDirectory;
    }
    public File getDirectory(){
        File dir=new File(context.getFilesDir(),setDirectory.toLowerCase());
        if(!dir.exists()) {
            dir.mkdir();
        }
        Log.e("QuestionBank called","directory---"+dir);
        return dir;
    }

    List<String> getTopicSets(File dir,List<String> setList){
        if(!dir.exists()) {
            Log.e("directory not exists","-----------"+dir);
            return setList;
        }
        try {
            File[] files= dir.listFiles((file,name)->
                    name.startsWith("set_")&& name.endsWith(".json")
                    );
            if(files.length!=0){
                for (int i = 1; i <= files.length; i++) {
                    String setName="set_"+i;
                    setList.add(setName.toLowerCase());
                }
//                Collections.sort(setList);
                return setList;
            }else{
                return setList;
            }
        }catch (Exception e){
            Log.e("getTopics","error "+e.getMessage());
            return setList;
        }
    }

    Map<String,List<ModelQuestion>> withDirection;
    List<ModelQuestion> noDirection;
    void groupByDirection(List<ModelQuestion> topicAllQuestions){
        withDirection= new LinkedHashMap<>();
        noDirection= new ArrayList<>();
        for(ModelQuestion q: topicAllQuestions){
            String queDirection=q.getDirection();
            if(queDirection!=null){
                if(queDirection.isEmpty()){
                    noDirection.add(q);
                }else{
                    withDirection.putIfAbsent(queDirection,new ArrayList<>());
                    withDirection.get(queDirection).add(q);
                }
            }else{
                noDirection.add(q);
            }
        }
    }
    public void generateQuestionSets(List<ModelQuestion> topicAllQuestions,File dir){
        int totalQue=topicAllQuestions.size();
        int setSize=25;
        int setCount=(int) Math.ceil((double)totalQue/setSize);

//---step 1 grouped/ nogrouped
        groupByDirection(topicAllQuestions);

//---step 2
        List<List<ModelQuestion>> blocks=new ArrayList<>();   //-------with direction as one block
        for(List<ModelQuestion> group: withDirection.values()){
            blocks.add(group);
        }
        for(ModelQuestion q: noDirection){                          //------no direction quesiton as single block
            blocks.add(Collections.singletonList(q));
        }

//---step 3
        Collections.shuffle(blocks);

//---step 4 Create set of 25 questions
        List<List<ModelQuestion>> QuestionSets = new ArrayList<>();
        List<ModelQuestion> currentSet = new ArrayList<>();
        int i=1;
        for(List<ModelQuestion> block: blocks){
            i++;
            if(currentSet.size()+block.size()>=setSize){                //minimum 25 question
                currentSet.addAll(block);
                QuestionSets.add(new ArrayList<>(currentSet));
                currentSet.clear();
            }
            else{
                currentSet.addAll(block);
            }
        }

        if(!currentSet.isEmpty()){
            if(currentSet.size()<=15){
                if(QuestionSets.isEmpty()){
                    QuestionSets.add(new ArrayList<>(currentSet));
                }
                else
                    QuestionSets.get(QuestionSets.size() - 1).addAll(currentSet);
            }else{
                QuestionSets.add(new ArrayList<>(currentSet));
            }
        }

        Gson gson = new Gson();                                 //--------------write set files
        int setNumber = 1;
        for (List<ModelQuestion> set : QuestionSets) {
            try {
                String json = gson.toJson(set);
                String newFilename = "set_" + setNumber + ".json";

                File newFile = new File(dir, newFilename.toLowerCase());
                if(newFile.exists()){
                    newFile.delete();
                }
                FileOutputStream newJsonFile = new FileOutputStream(newFile);
                newJsonFile.write(json.getBytes());
                newJsonFile.close();
                setNumber++;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    void loadQuestionSets(File dir,String fileName){
        try{
            String path = dir.getAbsolutePath() + "/" + fileName;

            File file = new File(path);
            FileInputStream fileInputStream=new FileInputStream(file);

            byte[] buffer=new byte[fileInputStream.available()];
            fileInputStream.read(buffer);
            fileInputStream.close();

            String json=new String(buffer);
            Gson gson=new Gson();
            Type type=new TypeToken<List<ModelQuestion>>(){}.getType();
            modelQuestionList=gson.fromJson(json,type);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("loadquestionsets ","error "+e.getMessage());
        }
    }


}
