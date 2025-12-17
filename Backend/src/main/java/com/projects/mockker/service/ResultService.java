package com.projects.mockker.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.projects.mockker.model.ResultModel;
import com.projects.mockker.repository.ResultRepository;

@Service
public class ResultService {
	@Autowired
	ResultRepository resultRepo;

	public List<ResultModel> getResultByUser(Long userId){								//---------------get Result by userId
		return resultRepo.findByUserIdOrderByDateTimeDesc(userId);
	}
	
	public ResultModel saveResult(ResultModel result) {								//---------------save result
	    ResultModel savedResult = resultRepo.save(result);
	    try {

		    List<ResultModel> results = resultRepo.findByUserIdOrderByDateTimeDesc(result.getUserId());


		    if (results.size() > 10) {
		        List<ResultModel> toDelete = results.subList(10, results.size());
		        resultRepo.deleteAll(toDelete);
		    }
	    }catch(Exception e) {
	    	
	    }

	    return savedResult;
	}
	@Transactional
	public void saveResults(List<ResultModel>  results){								//---------------save result
		Long userId=results.getFirst().getUserId();
		
		try {
			List<ResultModel> availableResults=resultRepo.findAllByUserId(userId);
			if(!availableResults.isEmpty()) 
				resultRepo.deleteAll(availableResults);
			else
				System.out.println("\t\t\t"+userId+":   result list is empty!");
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		finally {
			resultRepo.saveAll(results);
		}
	}
	
	

}
