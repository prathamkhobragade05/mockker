package com.projects.mockker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.mockker.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel,Long>{
	//query
	UserModel findByEmail(String email) ;
	
	boolean existsByEmail(String email);

}
