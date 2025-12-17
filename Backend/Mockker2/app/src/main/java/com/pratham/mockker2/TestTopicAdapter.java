package com.pratham.mockker2;

import static com.pratham.mockker2.Home.MODELTEST;
import static com.pratham.mockker2.Home.MODELTESTTOPIC;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TestTopicAdapter extends RecyclerView.Adapter<TestTopicAdapter.ViewHolder> {
    OnClickTopicListner listner;
    List<ModelTestTopic> topicList;
    Context context;

    public interface OnClickTopicListner{
        void onClickTopic(String topicName);
    }

    public TestTopicAdapter(List<ModelTestTopic> topicList, Context context, OnClickTopicListner listner) {
        this.topicList=topicList;
        this.context=context;
        this.listner=listner;
    }

    @NonNull
    @Override
    public TestTopicAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,viewGroup,false);
        return new TestTopicAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestTopicAdapter.ViewHolder viewHolder, int i) {
        ModelTestTopic topic= topicList.get(i);
        viewHolder.testName.setTextColor(ContextCompat.getColor(context,R.color.homeItem));
        viewHolder.testName.setText(topic.getTopic());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listner!=null){
                    MODELTESTTOPIC=topic;
                    listner.onClickTopic(MODELTESTTOPIC.getTopic());

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView testName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            testName=itemView.findViewById(android.R.id.text1);
        }
    }

}
