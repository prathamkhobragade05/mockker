package com.pratham.mockker2;

import static com.pratham.mockker2.Splash.UserId;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
    List<ModelResult> resultList;
    Context context;
    List<ModelTest> testList;
    List<ModelTestTopic> topicList;

    public ResultsAdapter(List<ModelResult> resultList, List<ModelTest> testList, List<ModelTestTopic> topicList, Context context) {
        this.resultList=resultList;
        this.testList=testList;
        this.topicList=topicList;
        this.context=context;
    }

    @NonNull
    @Override
    public ResultsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_result,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsAdapter.ViewHolder viewHolder, int i) {
        ModelResult result = resultList.get(i);

        for( ModelTest test:testList){
            if(result.getTestId().equals(test.getId())){
                String testName=test.getTest();
                viewHolder.test.setText(testName);
                for(ModelTestTopic topic:topicList){
                    if(result.getTopicId().equals(topic.getId())){
                        String topicName=topic.getTopic();
                        viewHolder.topic.setText(topicName);
                        break;
                    }
                }
                break;
            }
        }
        int score= Integer.parseInt(result.score);
        if(score<50){
            viewHolder.score.setTextColor(context.getColor(R.color.red));
        }else{
            viewHolder.score.setTextColor(context.getColor(R.color.green));
        }
        viewHolder.score.setText("SCORE "+result.score);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime date = LocalDateTime.parse(result.getDateTime(), formatter);

        viewHolder.date.setText(formatNiceDate(date));
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView test,score,date,set,topic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            topic= itemView.findViewById(R.id.topicName);
            set=itemView.findViewById(R.id.set);
            test=itemView.findViewById(R.id.testName);
            score=itemView.findViewById(R.id.score);
            date=itemView.findViewById(R.id.date);

        }
    }

    public static String formatNiceDate(LocalDateTime date) {

        LocalDate today = LocalDate.now();
        LocalDate dateOnly = date.toLocalDate();

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

        if (dateOnly.equals(today)) {
            return "Today, " + date.format(timeFormatter);
        }
        else if (dateOnly.equals(today.minusDays(1))) {
            return "Yesterday, " + date.format(timeFormatter);
        }
        else {
            return date.format(fullFormatter);
        }
    }


}
