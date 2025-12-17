package com.pratham.mockker2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServer {
                                                    //------------login
    @GET("user")
    Call<Long> onlyLogin(
            @Query("email") String email
    );

    @POST("user/login")
    Call<ModelUser> login(@Body CredentialModel credentialModel);

    @POST("user/login/forgetpass")
    Call<Boolean> forgetPass(@Body CredentialModel credentialModel);

    @POST("user/login/send-otp")
    Call<String> sendLoginOtp(@Body CredentialModel credentialModel);

    @POST("user/login/verify-otp")
    Call<ModelUser> verifyLoginOtp(@Body CredentialModel credentialModel);

                                                //------------register
    @POST("user/register")
    Call<ModelUser> register(@Body ModelUser user);


    @POST("user/register/send-otp")
    Call<String> sendRegisterOtp(@Body CredentialModel credentialModel);

    @POST("user/register/verify-otp")
    Call<String> verifyRegisterOtp(@Body CredentialModel credentialModel);

                                                    //------------tests
    @GET("test")
    Call<List<ModelTest>> getAllTests();

                                                    //------------topics
    @GET("test/topic")
    Call<List<ModelTestTopic>> getTopics(
            @Query("testid") Long test              //use Query for GET requestMapping

    );

                                                    //------------all questions
    @GET("question")
    Call<List<ModelQuestion>> getAllQuestions();

    @GET("question/test-topic")						//---------------questions by test and topic
    Call<List<ModelQuestion>> getQuestions(
            @Query("testid") Long test,
            @Query("topicid") Long topic
    );

    @POST("question/verfiyanswers")					//---------------verify answers
    Call<Map<String,Object>> verifyAnswers(@Body HashMap<String, String> answers);

                                                    //------------results
    @POST("result")
    Call<String> saveResult(@Body ModelResult modelResult);

    @POST("result/")
    Call<Void> saveResults(@Body List<ModelResult> modelResult);

    @GET("result/{userid}")
    Call<List<ModelResult>> getResult(
            @Path("userid") Long userId

    );
    @GET("database/{userid}")
    Call<ModelAllTalbes> getAllTables(
            @Path("userid") Long userId);

}
