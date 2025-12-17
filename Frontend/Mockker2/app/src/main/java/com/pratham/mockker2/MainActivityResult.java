package com.pratham.mockker2;

import static com.pratham.mockker2.Home.modelQuestionList;
import static com.pratham.mockker2.MainActivity.selectedResponse;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.concurrent.Executors;

public class MainActivityResult extends AppCompatActivity {
    ApiServer apiServer;
    TextView totalQ,correctA,wrongA,notA,score;
    static Button answerKey;
    static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiServer=ApiClient.getClient().create(ApiServer.class);

        totalQ=findViewById(R.id.totalQ);
        correctA=findViewById(R.id.correctA);
        wrongA=findViewById(R.id.wrongA);
        notA=findViewById(R.id.notA);
        score=findViewById(R.id.score);
        answerKey=findViewById(R.id.answerKey);
        progressBar=findViewById(R.id.progressBar);

        int totalq=getIntent().getIntExtra("total", 0);
        int correct=getIntent().getIntExtra("correct", 0);
        int wrong=getIntent().getIntExtra("wrong", 0);
        int notAtt=getIntent().getIntExtra("notA", 0);

        String totalQuestions = "Total Questions: "+ totalq;
        String correctAnswers = "Correct Answers: "+ correct;
        String wrongAnswers = "Wrong Answers: "+ wrong;
        String notAttemped = "Not Attempted: "+ notAtt;
        int scoree = ((correct * 100) / totalq);
        if(scoree<50){
            score.setTextColor(Color.RED);
        }else{
            score.setTextColor(Color.GREEN);
        }

        totalQ.setText(totalQuestions);
        correctA.setText(correctAnswers);
        wrongA.setText(wrongAnswers);
        notA.setText(notAttemped);
        score.setText("Score: "+scoree);

        answerKey.setOnClickListener(v -> {
            answerKey.setText("");
            progressBar.setVisibility(View.VISIBLE);

            Executors.newSingleThreadExecutor().execute(() -> {
                String message;
                boolean isSavedPdf;
                Uri pdfUri ;
                try {
                    AnswerKeyPdf pdf = new AnswerKeyPdf();
                    pdfUri=pdf.generatePdf(selectedResponse, modelQuestionList,MainActivityResult.this);
                } catch (Exception e) {
                    Log.e("MainActivity", "PDF error", e);
                    pdfUri=null;
                }
                Uri pdfFile=pdfUri;
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    answerKey.setText("Response Sheet ?");
                    if(pdfFile!=null){
                        Snackbar.make(findViewById(android.R.id.content), "PDF generated", Snackbar.LENGTH_LONG)
                                .setAction("OPEN", v1 -> openPdfFromMediaStore(this, pdfFile))
                                .show();
                    }else{
                        Snackbar.make(findViewById(android.R.id.content), "Failed to generate pdf", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
            });
        });
    }

    public void openPdfFromMediaStore(Context context, Uri pdfUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(intent, "Open PDF using");

        try {
            context.startActivity(chooser);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No app found to open PDF", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(MainActivityResult.this, Home.class);
        startActivity(intent);
        finish();
    }

}