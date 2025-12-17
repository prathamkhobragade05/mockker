package com.projects.mockker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projects.mockker.model.ResultModel;
import com.projects.mockker.repository.ResultRepository;
import com.projects.mockker.service.ResultService;

@RestController
@RequestMapping("/result")
public class ResultController {
	@Autowired
	ResultService resultService;
	@Autowired
	ResultRepository resultRepo;
	
	@PostMapping("")															//---------add result
	public ResponseEntity<?> result(@RequestBody ResultModel result){
		try {
			ResultModel resultModel=resultService.saveResult(result);
			return ResponseEntity.ok(resultModel);
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping("/")															//---------add result
	public ResponseEntity<?> results(@RequestBody List<ResultModel> results){
		try {
			resultService.saveResults(results);
			return ResponseEntity.ok("\t\tresult saved------");
		}catch(Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("")																//---------get results
	public List<ResultModel> getResult(){
		return resultRepo.findAll();
		
	}
	
	@GetMapping("/{userid}")													//---------results by userId
	public List<ResultModel> getUserResult(@PathVariable("userid") Long userId){
	    return resultService.getResultByUser(userId);
	}


}
