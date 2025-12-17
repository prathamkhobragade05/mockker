package com.pratham.mockker2;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;

import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pratham.mockker2.database.SaveToDatabase;

import java.util.concurrent.Executors;
import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends BaseActivity {
    ApiServer apiServer;
    Dialobox dialobox;

    TextView topTitle,topText1;

    RelativeLayout layoutName,layoutEmail, layoutPhone, layoutPassword;
    TextView labelName, labelEmail, labelPassword, labelPhone,login,passwordError;
    EditText editName, editEmail, editPassword, editPhone;
    Button Register;
    ProgressBar progressBar;
    FrameLayout frameLayout;

    String Name, Email,Phone,Password;
    boolean register=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiServer = ApiClient.getClient().create(ApiServer.class);
        dialobox =new Dialobox(this,intent -> dialogIntent(intent));

        topTitle=findViewById(R.id.topTitle);
        topText1=findViewById(R.id.topText1);
        frameLayout=findViewById(R.id.framelayout);
        layoutEmail=findViewById(R.id.layoutEmail);
        layoutName=findViewById(R.id.layoutName);
        layoutPhone=findViewById(R.id.layoutPhone);
        layoutPassword=findViewById(R.id.layoutPassword);
        passwordError=findViewById(R.id.passwordError);

        labelEmail=findViewById(R.id.labelEmail);
        progressBar=findViewById(R.id.progressBar);

        editName=findViewById(R.id.editName);
        editEmail=findViewById(R.id.editEmail);
        editPhone=findViewById(R.id.editPhone);
        editPassword=findViewById(R.id.editPassword);
        Register=findViewById(R.id.Register);
        login=findViewById(R.id.Login);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonRegister=Register.getText().toString();
                switch (buttonRegister) {
                    case "Send OTP":
                        sendOtp();
                        break;
                    case "Verify":
                        verifyOtp();
                        break;
                    case "Register":
                        register=true;
                        userRegister();
                        break;
                    default:
                        Log.e("error", "elseeee");
                        break;
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent=new Intent(Register.this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void sendOtp(){
        Email = editEmail.getText().toString().toLowerCase();
        Matcher email =Patterns.EMAIL_ADDRESS.matcher(Email);
        if(email.matches()){
            Register.setText("");
            showProgressBar();
            CredentialModel credentialModel=new CredentialModel(Email,null);
            Call<String> sendOtpCall= apiServer.sendRegisterOtp(credentialModel);
            sendOtpCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if(response.code()==409){
                        dialobox.AlertDialogBox("User Exist",Email+"\nis already registered\nPlease Login to Your Account","Login");
                        Register.setText("Send OTP");
                    }
                    else{
                        dialobox.AlertDialogBox("OTP Sent","otp sent to the\n"+Email+"\nKindly please check your \nInbox or Span"+" folder","Gmail");
                        editEmail.setHint("* * * * * *");
                        editEmail.setText(null);
                        labelEmail.setText("OTP");
                        topText1.setVisibility(View.VISIBLE);
                        Register.setText("Verify");
                    }
                    hideProgressBar();
                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {
                    dialobox.AlertDialogBox("Server Connection Failed","Kindly Please,\n\nCheck Your Internet Connection","Ok");
                    Log.e("error",throwable.getMessage().toString());
                    hideProgressBar();
                    Register.setText("Send OTP");
                }
            });
        }else{
            editEmail.setError("Enter valid email addresss!");
        }
    }
    private void verifyOtp(){
        Register.setText("");
        showProgressBar();
        String OTP = editEmail.getText().toString();
        CredentialModel credentialModel=new CredentialModel(Email,OTP);
        Call<String> verifyotpCall= apiServer.verifyRegisterOtp(credentialModel);
        verifyotpCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.isSuccessful()){
                    Toast.makeText(Register.this, "OTP Verified", Toast.LENGTH_SHORT).show();
                    layoutEmail.setVisibility(View.GONE);
                    layoutName.setVisibility(View.VISIBLE);
                    layoutPhone.setVisibility(View.VISIBLE);
                    layoutPassword.setVisibility(View.VISIBLE);
                    Register.setText("Register");
                    topText1.setText("Set password carefully, it cannot be changed later");
                }
                else if(response.code()==400){
                    dialobox.AlertDialogBox("Invalid OTP","kindly please enter correct otp.","ok");
                    Register.setText("Verify");
                }else{
                    
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                dialobox.AlertDialogBox("Server Connection Failed","Kindly Please,\n\nCheck Your Internet Connection","Ok");
                Register.setText("Verify");
                hideProgressBar();
            }
        });
    }

    private boolean credentials(){
        Name=editName.getText().toString();
        Phone=editPhone.getText().toString();
        Password=editPassword.getText().toString();
//        String passPattern=[];
        boolean isCredential=true;
        if(Name.isEmpty()){
            editName.setError("Name should not be empty!");
            isCredential=false;
        }
        if(Name.length()<10){
            editName.setError("Name should 10 character long!");
            isCredential=false;
        }
        if(Phone.length() != 10){
            editPhone.setError("Enter Valid Phone Number!");
            isCredential=false;
        }
        if(Password.length()<=6){
            passwordError.setTextColor(getColor(R.color.red));
            isCredential=false;
        }else{
            passwordError.setTextColor(getColor(R.color.textColor));
        }
        return isCredential;
    }
    private void userRegister() {
        if(credentials()) {
            Register.setText("");
            showProgressBar();
            ModelUser user = new ModelUser(null, Name, Email, Phone, Password);
            Call<ModelUser> call = apiServer.register(user);
            call.enqueue(new Callback<ModelUser>() {
                @Override
                public void onResponse(@NonNull Call<ModelUser> call, @NonNull Response<ModelUser> response) {
                    if (response.isSuccessful()) {
                        dialobox.AlertDialogBox("","Account Created.","ok");
                        ModelUser user=response.body();
                        saveUserId(user);

                    } else {
                        dialobox.AlertDialogBox("Failed","Account Creation Failed!\nPlease Try Again Later...","ok");
                        Register.setText("Register");
                    }
                }

                @Override
                public void onFailure(Call<ModelUser> call, Throwable throwable) {
                    dialobox.AlertDialogBox("Server Connection Failed","Kindly Please,\n\nCheck Your Internet Connection","Ok");
                    Register.setText("Register");
                }
            });
        }
    }
    private void saveUserId(ModelUser user){
        Log.e("Login save userId 321", user.getId().toString()+" ---- "+user.getName());
        runOnUiThread(()->{
            showProgressBar();
        });
        Executors.newSingleThreadExecutor().execute(()->{

            SaveToDatabase.saveUserId(user.getId(),user.getName());
            runOnUiThread(()->{
                hideProgressBar();
            });
            Intent intent =new Intent(Register.this,Splash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
    void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.VISIBLE);
        frameLayout.setClickable(true);
        frameLayout.setFocusable(true);
    }
    void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        frameLayout.setClickable(false);
        frameLayout.setFocusable(false);
    }

    private void dialogIntent(Intent intent){
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Gmail app not installed", Toast.LENGTH_SHORT).show();
        }
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