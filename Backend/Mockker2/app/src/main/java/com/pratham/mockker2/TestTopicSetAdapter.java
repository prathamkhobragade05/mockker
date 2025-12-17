package com.pratham.mockker2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TestTopicSetAdapter extends RecyclerView.Adapter<TestTopicSetAdapter.ViewHolder> {
    List<String> setList;
    Context context;
    OnClickSetListner listner;

    public interface OnClickSetListner{
        void onClickSet(String set);
    }
    public TestTopicSetAdapter(List<String> setList, Context context,OnClickSetListner listner) {
        this.setList=setList;
        this.context=context;
        this.listner=listner;
    }

    @NonNull
    @Override
    public TestTopicSetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,viewGroup,false);
        return new TestTopicSetAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestTopicSetAdapter.ViewHolder viewHolder, int i) {
        String set=setList.get(i);
        viewHolder.testName.setTextColor(ContextCompat.getColor(context,R.color.homeItem));
        viewHolder.testName.setText(set.toUpperCase());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.onClickSet(set);
            }
        });
    }

    @Override
    public int getItemCount() {
        return setList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView testName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            testName=itemView.findViewById(android.R.id.text1);
        }
    }
}
