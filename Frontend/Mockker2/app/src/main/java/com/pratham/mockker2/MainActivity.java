package com.pratham.mockker2;


import static com.google.android.material.internal.ViewUtils.hideKeyboard;
import static com.pratham.mockker2.Home.modelQuestionList;
import static com.pratham.mockker2.Splash.UserId;
import static com.pratham.mockker2.Splash.appDatabase;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pratham.mockker2.database.AllEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ApiServer apiServer;

    TextView direction,questionNo,totalQuestion,questionText,timer,clearR;
    Button previous,next;
    RadioGroup optionGroup;
    ProgressBar progressBar;

    int currentIndex=0;
    public static HashMap<String,String> selectedResponse;

    int totalQuestions;         //-------from previous intent
    Long testId;
    Long topicId;
    String setItem;

    boolean timeOut=false;
    boolean exitTest=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiServer = ApiClient.getClient().create(ApiServer.class);

        timer =findViewById(R.id.timer);
        progressBar=findViewById(R.id.progressBar);
        direction=findViewById(R.id.direction);
        questionNo =findViewById(R.id.currentQue);
        totalQuestion =findViewById(R.id.totalQuestions);
        questionText = findViewById(R.id.question);
        optionGroup = findViewById(R.id.options);
        clearR=findViewById(R.id.clearR);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);

        selectedResponse=new HashMap<>();


        totalQuestions=modelQuestionList.size();
        totalQuestion.setText("/"+totalQuestions);
        showQuestion(currentIndex);

        testId=getIntent().getLongExtra("testid",0L);
        topicId=getIntent().getLongExtra("topicid",0L);
        setItem=getIntent().getStringExtra("set");

        setTimerDialogBox("Set Timer","Total Questions: "+totalQuestions+"\n","Set Timer");

        clearR.setOnClickListener(v -> {
            optionGroup.clearCheck();  // clears the selected radio button
        });

//----------- Handle Next button
        next.setOnClickListener(v -> {
            saveSelectedOption();
            if (currentIndex < modelQuestionList.size() - 1) {
                currentIndex++;
                if(currentIndex== modelQuestionList.size() - 1){
                    next.setText("Submit");
                }
                showQuestion(currentIndex);
            } else {
                //submit activity
                String message="Submit Test!";
                boolean dialogCancel=true;
                if(timeOut){
                    message="Time Out!";
                    dialogCancel=false;
                } else if (exitTest) {
                    progressBar.setVisibility(View.VISIBLE);
                    offlineAnalysTest(UserId);
                }

                if(!exitTest)
                    new AlertDialog.Builder(this)
                        .setTitle("Submit Test")
                        .setMessage(message)
                        .setCancelable(dialogCancel)
                        .setPositiveButton("OK", (dialog, which) -> {
//------------------submit ansers
                            progressBar.setVisibility(View.VISIBLE);
                            offlineAnalysTest(UserId);
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex>0){
                    currentIndex--;
                    if(currentIndex!= modelQuestionList.size() - 1){
                        next.setText("Next");
                    }
                    showQuestion(currentIndex);
                }
            }
        });
    }

    private void showQuestion(int index) {
        ModelQuestion modelQuestion = modelQuestionList.get(index);
        questionNo.setText("Q"+ (currentIndex+1));
        direction.setText(modelQuestion.getDirection());
        questionText.setText(modelQuestion.getQuestion());

        optionGroup.removeAllViews();        // -------Clear old options
        // Add new options
        optionGroup.clearCheck();
        for (String opt : modelQuestion.getOptions()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(opt);
            radioButton.setTextSize(16);
            optionGroup.addView(radioButton);

            radioButton.setButtonTintList(
                    ContextCompat.getColorStateList(this, R.color.radio_selector)           //radio button color select/unselect
            );

            if (opt.equals(modelQuestion.getSelected())) {          //-------Restore previously selected option
                radioButton.setChecked(true);
            }
        }
    }

    private void saveSelectedOption() {
        int selectedId = optionGroup.getCheckedRadioButtonId();
        Long questionId= modelQuestionList.get(currentIndex).getId();

        if (selectedId != -1) {
            RadioButton selected = findViewById(selectedId);
            try{
                String selectedAnswer = selected.getText().toString();
                modelQuestionList.get(currentIndex).setSelected(selectedAnswer);
                selectedResponse.put(String.valueOf(questionId),selectedAnswer);
            }catch (Exception e){
                Toast.makeText(this, "unable to save option\n contact to admin", Toast.LENGTH_SHORT).show();
            }

        } else {
            modelQuestionList.get(currentIndex).setSelected(null);
            selectedResponse.put(String.valueOf(questionId),"");
        }
    }

    private void offlineAnalysTest(long userId) {
        Map<String, Boolean> response = new HashMap<>();
        int correct = 0;
        int wrong = 0;
        int notAnswered = 0;
        for (ModelQuestion p : modelQuestionList) {
            Long questionId = p.getId();
            String questionAnswer = p.getAnswer();
            for (Map.Entry<String, String> entry : selectedResponse.entrySet()) {
                String userQuestionId = entry.getKey();
                String userAnswer = entry.getValue();
                if (questionId.toString().equals(userQuestionId)) {
                    boolean isCorrect=questionAnswer.equals(userAnswer);
                    if(isCorrect){
                        correct++;
                    }
                    else {
                        if (userAnswer.equals("")) {
                            notAnswered++;
                        } else {
                            wrong++;
                        }
                    }
                    break;
                }
            }
        }

        int score = (correct * 100) / modelQuestionList.size();

        if(exitTest){
            intent = new Intent(MainActivity.this, Home.class);
        }
        else{
            intent = new Intent(MainActivity.this, MainActivityResult.class);
        }
//        intent = new Intent(MainActivity.this, MainActivityResult.class);
        intent.putExtra("userId",userId);
        intent.putExtra("total", modelQuestionList.size());
        intent.putExtra("correct",correct);
        intent.putExtra("wrong",wrong);
        intent.putExtra("notA",notAnswered);

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = now.format(formatter);

        try{
            AllEntity.ResultEntity resultEntity= new AllEntity.ResultEntity();
            resultEntity.testId=testId;
            resultEntity.topicId=topicId;
            resultEntity.userId=userId;
            resultEntity.score=String.valueOf(score);
            resultEntity.date=formattedDate;
            Executors.newSingleThreadExecutor().execute(()->{
                appDatabase.resultDao().insertResult(resultEntity);
                List<AllEntity.ResultEntity> resultEntityList=appDatabase.resultDao().getAllResults();
                int resultCount=resultEntityList.size();

                if((resultCount)>3){
                    try{
                        List<AllEntity.ResultEntity> resultToDelete= resultEntityList.subList(3,resultEntityList.size());
                        for(AllEntity.ResultEntity result:resultToDelete){
                            boolean isDeleted=appDatabase.resultDao().deleteResult(result.resultId)>0;
                            Log.e("BaseAcitvity 66","deleted--- "+result.resultId+" -- "+isDeleted);
                        }
                    }catch (Exception e){
                        Log.e("BaseActivity2 67",e.getMessage());
                    }
                }
            });
        }catch (Exception e){
            Log.e("save results",e.getMessage());
        }
        progressBar.setVisibility(View.GONE);
        startActivity(intent);
        finish();
    }

    Intent intent;
    void hideKeyboard(EditText inputTimer){
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null ) {
            imm.hideSoftInputFromWindow(inputTimer.getWindowToken(), 0);
        }
    }

    void timer(Integer time){
        // 20 min = 20*60*1000    milliseconds
        long totalTime = time*60*1000;    //milliseconds

        CountDownTimer countDownTimer = new CountDownTimer(totalTime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                //millis to mm:ss
                long seconds=(millisUntilFinished/1000);

                int hour= (int) seconds/3600;
                int min = (int) (seconds%3600) /60;
                int sec = (int) seconds%60;

                String timeFormatted;
                if(time>60){
                    timeFormatted = String.format("%02d:%02d:%02d",hour,min,sec);
                }else{
                    timeFormatted = String.format("%02d:%02d",min,sec);
                }

                if(min==4&&sec== 59){
                    timer.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.red));
                }
                MainActivity.this.timer.setText(timeFormatted);
            }

            @Override
            public void onFinish() {
                //auto submit
                for(int i=currentIndex;i<modelQuestionList.size();i++){
                    if(i==(modelQuestionList.size()-1)) timeOut=true;
                    next.performClick();
                }
            }
        }.start();
    }
    private void setTimerDialogBox(String title,String message,String button){
        LayoutInflater inflater=LayoutInflater.from(this);
        View view=inflater.inflate(R.layout.dialogbox,null);
        EditText inputTimer=view.findViewById(R.id.inputTimer);
        TextView textTitle=view.findViewById(R.id.title);
        TextView textMessage=view.findViewById(R.id.message);
        LinearLayout linearLayout=view.findViewById(R.id.input);
        Button cancel=view.findViewById(R.id.cancel);
        Button ok=view.findViewById(R.id.ok);
        ok.setVisibility(View.VISIBLE);

        textTitle.setText(title);
        textMessage.setText(message);
        ok.setText(button);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this).setView(view)
                .setCancelable(false);

        AlertDialog dialog=builder.create();
        dialog.show();

        if(warning){
            linearLayout.setVisibility(View.GONE);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(warning){
                    warning=false;
                    dialog.dismiss();
                    return;
                }else if(ok.getText().toString().equals("Yes")|| cancel.getText().toString().equals("Back")){
                    linearLayout.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    setTimerDialogBox("Set Timer","Total Questions: "+totalQuestions+"\n","Set Timer");
                    return;
                }

                linearLayout.setVisibility(View.GONE);
                hideKeyboard(inputTimer);
                textTitle.setText("Exist Test!");
                textMessage.setText("Are you sure, You want to exit?");
                ok.setText("Yes");
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(inputTimer);
                if(ok.getText().toString().equals("Yes")){
                    if(warning){
                        for(int i=currentIndex;i<modelQuestionList.size();i++){
                            if(i==(modelQuestionList.size()-1)) exitTest=true;
                            next.performClick();
                        }
                        Toast.makeText(MainActivity.this, "Exit Test", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }
                    dialog.dismiss();
                    startActivity(new Intent(MainActivity.this,Home.class));
                    finish();
                }
                int timer= Integer.parseInt(inputTimer.getText().toString());
                if(!ok.getText().toString().equals("Start Test")){
                    if(timer<20){
                        inputTimer.setText("20");
                        inputTimer.setError("min time: 20 min");
                        return;
                    }
                    if (timer>90) {
                        inputTimer.setText("20");
                        inputTimer.setError("max time: 90 min");
                        return;
                    }

                    textTitle.setText("Mock Test");
                    textMessage.setText("Best Of Luck");
                    cancel.setText("Back");
                    ok.setText("Start Test");
                    linearLayout.setVisibility(View.GONE);
                }else{
                    showQuestion(currentIndex);
                    timer(timer);
                    dialog.dismiss();
                }
            }
        });
    }
    boolean performBack=false;
    boolean warning=false;
    @Override
    public void onBackPressed() {
        if(performBack){
            super.onBackPressed();
        }
        warning=true;
        setTimerDialogBox("Exit Test?","Are you sure, You want to exit?\nTest Will Be Auto Submitted","Yes");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if(getCurrentFocus()!=null){
            InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
        return super.dispatchTouchEvent(event);
    }
}