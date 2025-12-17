package com.pratham.mockker2;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Dialobox {
    Context context;
    onButtonClickListner listner;

    EditText inputTimer;

    public Dialobox(Context context,onButtonClickListner listner){
        this.context=context;
        this.listner=listner;
    }

    public interface onButtonClickListner{
        void onButtonClick(Intent intent);
    }

    void AlertDialogBox(String title,String message,String buttonText){

        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.dialogbox,null);
        inputTimer=view.findViewById(R.id.inputTimer);
        TextView textTitle=view.findViewById(R.id.title);
        TextView textMessage=view.findViewById(R.id.message);
        LinearLayout linearLayout=view.findViewById(R.id.input);
        Button cancel=view.findViewById(R.id.cancel);
        Button ok=view.findViewById(R.id.ok);

//        cancel.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);

        textTitle.setText(title);
        textMessage.setText(message);
        ok.setText(buttonText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(view)
                .setCancelable(true);

        AlertDialog dialog=builder.create();
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {              //----------- cancel button
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {              //----------- ok button
            @Override
            public void onClick(View v) {

                if(ok.getText().toString().equals("Login")){
                    if(listner!=null){
                        Intent intent=new Intent(context,Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        listner.onButtonClick(intent);
                    }
                }
                if(ok.getText().toString().equals("Register")){
                    if(listner!=null){
                        Intent intent=new Intent(context,Register.class);
                        listner.onButtonClick(intent);
                    }
                }
                if(ok.getText().toString().equals("Gmail")){
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                    intent.setPackage("com.google.android.gm");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    listner.onButtonClick(intent);
                }
                dialog.dismiss();
            }
        });
    }
}
