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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pratham.mockker2.database.SaveToDatabase;

import java.util.concurrent.Executors;
import java.util.regex.Matcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends BaseActivity {
    ApiServer apiServer;
    Dialobox dialobox;

    TextView topTitle,topText1,topText2;

    RelativeLayout layoutEmail,layoutPasword;
    EditText editEmail,editPassword;
    TextView register,forgetPass,lableEmail;
    Button login;
    ToggleButton toggleButton;
    String Email,Otp,Password;
    ProgressBar progressBar;
    FrameLayout frameLayout;

    boolean forPass=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiServer = ApiClient.getClient().create(ApiServer.class);

        progressBar=findViewById(R.id.progressBar);
        topTitle=findViewById(R.id.topTitle);
        topText1=findViewById(R.id.topText1);
        topText2=findViewById(R.id.topText2);
        frameLayout=findViewById(R.id.framelayout);

        layoutEmail=findViewById(R.id.layoutEmail);
        layoutPasword=findViewById(R.id.layoutPassword);
        toggleButton=findViewById(R.id.toggle);
        lableEmail=findViewById(R.id.labelEmail);
        editEmail=findViewById(R.id.editEmail);
        editPassword=findViewById(R.id.editPassword);
        forgetPass=findViewById(R.id.forPass);
        login=findViewById(R.id.Login);
        register=findViewById(R.id.Register);

        dialobox =new Dialobox(this, this::dialogIntent);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Loginbutton=login.getText().toString();
                switch (Loginbutton){
                    case "Send OTP" :
                        sendOtp();
                        break;

                    case "Verify" :
                        verifyOtp();
                        break;

                    case "Login" :
                        userLogin();
                        break;

                    case "Send Password":
                        forgetPass();
                    default:
                        break;
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    topTitle.setText("Login Via OTP");
                    topText1.setVisibility(View.GONE);
                    layoutPasword.setVisibility(View.GONE);
                    forgetPass.setVisibility(View.GONE);
                    editPassword.setText("");
                    login.setText("Send OTP");
                }else{
                    topTitle.setText("Login Via Password");
                    topText1.setVisibility(View.GONE);
                    layoutPasword.setVisibility(View.VISIBLE);
                    forgetPass.setVisibility(View.VISIBLE);
                    lableEmail.setText("Email");
                    login.setText("Login");
                }
            }
        });

        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutPasword.setVisibility(View.GONE);
                forgetPass.setVisibility(View.GONE);
                toggleButton.setChecked(true);
                login.setText("Send Password");

                topTitle.setText("Find your account");
                topText1.setVisibility(View.VISIBLE);
            }
        });
    }


    private void forgetPass(){
        Email=editEmail.getText().toString().toLowerCase();
        Matcher email = Patterns.EMAIL_ADDRESS.matcher(Email);
        if(email.matches()){
            login.setText("");
            showProgressBar();
            CredentialModel credential=new CredentialModel(Email,null);
            Call<Boolean> forgetpassCall=apiServer.forgetPass(credential);
            forgetpassCall.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if(response.isSuccessful()){
                        Boolean isSent=response.body();
                        if(isSent){
                            dialobox.AlertDialogBox("OTP Sent","Password sent to\n"+Email+"\nKindly please check your inbox  or spam folder","Gmail");
                            startActivity(new Intent(Login.this,Login.class));
                            finish();
                        }else{
                            login.setText("Send Password");
                            dialobox.AlertDialogBox("User Not Exist", Email+" Not Exist\n Please Enter Correct Email Or Register Yourself","Register");
                        }
                    }else{
                        login.setText("Send Password");
                    }
                    hideProgressBar();
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable throwable) {
                    login.setText("Send Password");
                    Log.e("Login forgetPass",throwable.getMessage());
                    dialobox.AlertDialogBox("Server Connection Failed","Kindly Please,\n\nCheck Your Internet Connection","Ok");
                    hideProgressBar();
                }
            });
        }else{
            editEmail.setError("Enter valid email address");
        }

    }

    private boolean credentials(){
        Email = editEmail.getText().toString().toLowerCase();
        Matcher email = Patterns.EMAIL_ADDRESS.matcher(Email);
        Password=editPassword.getText().toString();
        boolean isCredential=true;

        if(!email.matches()){
            isCredential=false;
            editEmail.setError("Enter valid email address");
        }
        if(Password.length()<=6){
            editPassword.setError("password length should be minimumn 7 charector long.\n It contains at least on uppercase,lowercase, number and special charecter.");
            isCredential=false;
        }
        return isCredential;
    }

    private void userLogin() {
        Email=editEmail.getText().toString().toLowerCase();
        Password=editPassword.getText().toString();
        if(credentials()){
            showProgressBar();
            login.setText("");
            CredentialModel credential=new CredentialModel(Email,Password);
            Call<ModelUser> call=apiServer.login(credential);
            call.enqueue(new Callback<ModelUser>() {
                @Override
                public void onResponse(@NonNull Call<ModelUser> call, @NonNull Response<ModelUser> response) {
                    ModelUser user = response.body();
                    if(user.getId()==-1L){
                        login.setText("Login");
                        dialobox.AlertDialogBox("User Not Exist", "Please Enter Correct Email Or Register YourSelf", "Register");
                    } else if(user.getId()==0L){
                        login.setText("Login");
                        dialobox.AlertDialogBox("Incorrect Credentials!", "Incorrect Passwrod !", "ok");
                    }else{
                        saveUserId(user);
                    }
                    hideProgressBar();
                }
                @Override
                public void onFailure(@NonNull Call<ModelUser> call, @NonNull Throwable throwable) {
                    login.setText("Login");
                    Log.e("user login error",throwable.getMessage());
                    dialobox.AlertDialogBox("Connection Timeout","Kindly Please,\nCheck Your Internet Connection And Try Again Later.","Ok");
                    hideProgressBar();
                }
            });
        }
    }
    public void sendOtp() {
        Email = editEmail.getText().toString().toLowerCase();
        Matcher email = Patterns.EMAIL_ADDRESS.matcher(Email);

        if(email.matches()){
            login.setText("");
            showProgressBar();
            CredentialModel credentialModel=new CredentialModel(Email,null);
            Call<String> sendOtpCall = apiServer.sendLoginOtp(credentialModel);
            sendOtpCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        lableEmail.setText("OTP");
                        login.setText("Verify");
                        editEmail.setText(null);
                        editEmail.setHint("* * * * * *");
                        dialobox.AlertDialogBox("OTP Sent","otp sent to the\n"+Email+"\nKindly please check your \nInbox or Span"+" folder","Gmail");
                    } else {
                        login.setText("Send OTP");
                        dialobox.AlertDialogBox("User Not Exist", Email+" Not Exist\n Please Enter Correct Email Or Register YourSelf.","Register");
                    }
                    hideProgressBar();
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable throwable) {
                    login.setText("Send OTP");
                    Log.e("server error", throwable.getMessage());
                    String title="Server Connection Failed";
                    String message="Kindly Please,\n\nCheck Your Internet Connection";
                    if(throwable.getMessage().equals("timeout")){
                        title="Server Not Respond";
                        message="Problem in server\nPlease Try after some time.";
                    }
                    dialobox.AlertDialogBox(title,message,"Ok");
                    hideProgressBar();
                }
            });
        }else{
            editEmail.setError("Enter valid email addresss!");
        }
    }
    private void verifyOtp(){
        showProgressBar();
        Otp=editEmail.getText().toString();
        CredentialModel credential= new CredentialModel(Email,Otp);
        Call<ModelUser> verifyOtp=apiServer.verifyLoginOtp(credential);
        verifyOtp.enqueue(new Callback<ModelUser>() {
            @Override
            public void onResponse(Call<ModelUser> call, Response<ModelUser> response) {
                if(response.isSuccessful()){
                    ModelUser user=response.body();

                    if(user==null){
                        dialobox.AlertDialogBox("Server Problem","please try again later.","ok");
                    }
                    else if(user.getId()==0){
                        dialobox.AlertDialogBox("Invalid OTP","kindly please Enter Correct OTP.","ok");
                    }else{
                        saveUserId(user);
                    }
                }else{
                    dialobox.AlertDialogBox("Invalid OTP not success","kindly please Enter Correct OTP.","ok");
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<ModelUser> call, Throwable throwable) {
                dialobox.AlertDialogBox("Server Connection Failed","Kindly Please,\n\nCheck Your Internet Connection","Ok");
                hideProgressBar();
            }
        });
    }

    private void saveUserId(ModelUser user){
        Log.e("Login save userId 321", user.getId().toString()+" ---- "+user.getName());
        Executors.newSingleThreadExecutor().execute(()->{
            runOnUiThread(()->{
                showProgressBar();
            });
            SaveToDatabase.saveUserId(user.getId(),user.getName());
            Intent intent =new Intent(Login.this,Splash.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.VISIBLE);
        toggleButton.setEnabled(false);
        frameLayout.setClickable(true);
        frameLayout.setFocusable(true);
    }
    void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
        frameLayout.setVisibility(View.GONE);
        toggleButton.setEnabled(true);
        frameLayout.setClickable(false);
        frameLayout.setFocusable(false);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if(getCurrentFocus()!=null){
            InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
        return super.dispatchTouchEvent(event);
    }

    private void dialogIntent(Intent intent){
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Gmail app not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
//        layoutPasword.setVisibility(View.VISIBLE);
//        forgetPass.setVisibility(View.VISIBLE);
//        toggleButton.setChecked(false);
//        lableEmail.setText("Email");
//        login.setText("Login");
    }

    @Override
    public void onBackPressed(){
        if(login.getText().toString().equals("Login"))
            super.onBackPressed();

        topTitle.setText("Login Via Password");
        topText1.setVisibility(View.GONE);
        layoutPasword.setVisibility(View.VISIBLE);
        forgetPass.setVisibility(View.VISIBLE);
        toggleButton.setChecked(false);
        lableEmail.setText("Email");
        login.setText("Login");
    }
}