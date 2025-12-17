package com.projects.mockker.model;

public class CredentialModel {
	String email;
	String otp_password;
	
	public CredentialModel(String email,String otp_password) {
		this.email=email;
		this.otp_password=otp_password;
	}
	
	public CredentialModel() {}
	public String getEmail() {return email;}
	public String getOtp_password() {return otp_password;}
}
