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

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> {
    onTestClickListner listner;
    private List<ModelTest> modelTest;
    Context context;

    public interface onTestClickListner{
        void onTestClick(ModelTest test);
    }

    public TestAdapter(List<ModelTest> modelTest,Context context,onTestClickListner listner){
        this.modelTest=modelTest;
        this.context=context;
        this.listner=listner;
    }

    @NonNull
    @Override
    public TestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TestAdapter.ViewHolder viewHolder, int i) {
        ModelTest test= modelTest.get(i);
        viewHolder.testName.setTextColor(ContextCompat.getColor(context,R.color.homeItem));
        viewHolder.testName.setText(test.getTest());
        viewHolder.testName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listner!=null){
                    listner.onTestClick(test);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelTest.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView testName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            testName=itemView.findViewById(android.R.id.text1);

        }
    }
}
