package com.projects.mockker.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projects.mockker.model.QuestionModel;
import com.projects.mockker.model.TestModel;
import com.projects.mockker.model.TestTopicModel;
import com.projects.mockker.service.TestService;

@RestController
@RequestMapping("/test")
public class TestController {
	@Autowired
	TestService testService;
	
	@GetMapping("")																//---------get all tests
	public ResponseEntity<List<TestModel>> getAllTests(){
		return ResponseEntity.ok(testService.getAlltests());
	}
	
	@PostMapping("")																//---------add new test
    public TestModel addTest(@RequestBody TestModel testModel) {
        return testService.addTest(testModel);
    }
	
	
	@PostMapping("/")																//---------add multiple new tests
    public ResponseEntity<?> addTests(@RequestBody List<TestModel> tests){
    	try {
    		List<TestModel> addedTests= testService.addTests(tests);
    		return ResponseEntity.ok(addedTests);
    	}catch(Exception e) {
    		return ResponseEntity.badRequest().body(e.getMessage());
    	}
    }
	
//----------------------topics
	
    @GetMapping("/topic")																//---------get topics by testId
    public ResponseEntity<List<TestTopicModel>> getTestTopics(@RequestParam("testid") Long test) {
    	List<TestTopicModel> topics=testService.getTopicsByTest(test);
    	
    	if(topics.isEmpty()) {
            return ResponseEntity.notFound().build();

    	}else {
          return ResponseEntity.ok(topics);
    	}
    }
    
	@PostMapping("/topic")																//---------add new test
    public TestTopicModel addtopic(@RequestBody TestTopicModel topic) {
        return testService.addTopic(topic);
    }
    
    @PostMapping("/topic/")																//---------add multiple new tests
    public ResponseEntity<?> addTopics(@RequestBody List<TestTopicModel> topics){
    	try {
    		List<TestTopicModel> addedTests= testService.addTopicsByTest(topics);
    		return ResponseEntity.ok(addedTests);
    	}catch(Exception e) {
    		return ResponseEntity.badRequest().body(e.getMessage());
    	}
    }



}
