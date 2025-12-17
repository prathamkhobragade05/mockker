package com.projects.mockker.service;

import java.io.IOException;
import java.util.HashMap;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

@Service
public class OtpEmailService {

	@Value("${SENDGRID_API}")
   	private String sendGridApiKey;
	
	@Autowired
	UserService userService;
	
	private Map<String,String> otpStorage=new HashMap<>();

	public String generateOtp(String email) {											//---------------generate OTP
		String otp =String.valueOf((int)(Math.random()*900000)+100000);
		otpStorage.put(email, otp);
		return otp;
	}
	
	@Async
	public void sendEmailOtp(String email, String otp) {											//---------------send OTP Email
		Email from = new Email("mailservice.softara@gmail.com");
        String subject = "Mockker";
        Email to = new Email(email);
		Content content = new Content("text/plain", "Your OTP is: " + otp);
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

		try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("Status Code: " + response.getStatusCode());
            System.out.println("Body: " + response.getBody());
            System.out.println("Headers: " + response.getHeaders());

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
	}
	
	public boolean verifyOtpLogin(String email, String otp) {											//---------------verify OTP
		return otp.equals(otpStorage.get(email));

	}

	public boolean verifyOtpRegister(String email, String otp) {											//---------------verify OTP
		return otp.equals(otpStorage.get(email))? true:false;
	}

	public Boolean sendPasswordLogin(String email,String Password) {
		Email from = new Email("mailservice.softara@gmail.com");
		String subject = "Mockker";
	    Email to = new Email(email);
		Content content = new Content("text/plain", "Your password is:"+Password);
	    Mail mail = new Mail(from, subject, to, content);
	
	    SendGrid sg = new SendGrid(sendGridApiKey);
	    Request request = new Request();
	
		try {
	        request.setMethod(Method.POST);
	        request.setEndpoint("mail/send");
	        request.setBody(mail.build());
	
	        Response response = sg.api(request);
	
	        System.out.println("Status Code: " + response.getStatusCode());
	        System.out.println("Body: " + response.getBody());
	        System.out.println("Headers: " + response.getHeaders());
			return true;
	
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
			return false;

	    }
		
	}
	
	

}
