package com.projects.mockker.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.projects.mockker.model.QuestionModel;
import com.projects.mockker.service.QuestionService;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    
    @GetMapping("")																//---------all questions
    public ResponseEntity<List<QuestionModel>> getAllQuestions() {
        List<QuestionModel> list = questionService.getAllQuestions();
        return ResponseEntity.ok(list); // list may be empty → still fine
    }
    
    @GetMapping("/{id}")																//---------question by id
    public ResponseEntity<?> getQuestionById(@PathVariable("id") Long id) {
        Optional<QuestionModel> question = questionService.getQuestionById(id);

        if (question.isPresent()) {
            return ResponseEntity.ok(question.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/test-topic")																//---------by test and topic 
    public ResponseEntity<List<QuestionModel>> getQuestions(@RequestParam("testid") Long test, @RequestParam("topicid") Long topic) {
        List<QuestionModel> question = questionService.getQuestionByTopic(test,topic);
        return ResponseEntity.ok(question); 
    }
    
    @PostMapping("/add")																//---------add question
    public ResponseEntity<?> addQuestion(@RequestBody QuestionModel question){
    	try {
    		QuestionModel addedQuestion = questionService.addQuestion(question);
    		return ResponseEntity.ok(addedQuestion);
    	}catch(Exception e) {
    		return ResponseEntity.badRequest().body(e.getMessage());
    	}
    	
    }
    
    @PostMapping("/addQuestions")																//---------add multiple questions
    public ResponseEntity<?> addQuestions(@RequestBody List<QuestionModel> questions){
    	try {
    		List<QuestionModel> addedQuestions = questionService.addQuestions(questions);
    		return ResponseEntity.ok(addedQuestions);
    	}catch(Exception e) {
    		return ResponseEntity.badRequest().body(e.getMessage());
    	}
    	
    }
    
    @PostMapping("/verfiyanswers")																//---------verify answers
    public ResponseEntity<?> verifyAnswers(@RequestBody Map<String ,String> userAnswers){
    		return ResponseEntity.ok(questionService.verfiyAnswers(userAnswers));
    	
    }
}
