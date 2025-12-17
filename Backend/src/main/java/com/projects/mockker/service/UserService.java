package com.projects.mockker.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mockker.model.UserModel;
import com.projects.mockker.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	UserRepository userRepo;
	
	
	public boolean isExistsEmail(String email) {
		return userRepo.existsByEmail(email);
	}
	
	public UserModel loginPassword(String email,String password) {			//---------------login via password
		UserModel user= userRepo.findByEmail(email);
//		Long UserId=0L;
		if(user.getPassword().equals(password)) {
//			UserId=user.getId();
			return user;
		}
		else {
			user.setId(0L);
			return user;
		}
	}
	
	public UserModel LoginOtp(String email) {			//---------------login via OTP
		UserModel user=userRepo.findByEmail(email);
		if(user!=null) {
			return user;
		}else {
			return null;
		}
	}
	
	
	public UserModel registerUser(UserModel user) throws Exception {			//---------------register
		// TODO Auto-generated method stub
		if(userRepo.existsByEmail(user.getEmail())) {
			throw new Exception(user.getEmail()+" already registered!");
		}
		userRepo.save(user);

		return userRepo.save(user);
	}

	public UserModel getUser(String email) {
		UserModel user =userRepo.findByEmail(email);
		return user;
	}
	
}
