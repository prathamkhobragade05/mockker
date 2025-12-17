package com.projects.mockker.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mockker.model.QuestionModel;
import com.projects.mockker.model.TestTopicModel;
import com.projects.mockker.repository.QuestionRepository;

@Service
public class QuestionService {
    @Autowired
    QuestionRepository questionRepo;
    
    public List<QuestionModel> getAllQuestions() {											//---------------all questions
        return questionRepo.findAll();
    }
    
    public Optional<QuestionModel> getQuestionById(Long id) {								//---------------question by id
    	Optional<QuestionModel> question=questionRepo.findById(id);
        return  question;
    }

    public List<QuestionModel> getQuestionByTopic(Long test, Long topic) {					//---------------question by testId and topicId
    	List<QuestionModel> question= questionRepo.findByTestAndTopic(test,topic);
		return question;
	}
    
	public QuestionModel addQuestion(QuestionModel question) {								//---------------add single question
		return questionRepo.save(question);
	}

	public List<QuestionModel> addQuestions(List<QuestionModel> questions) {				//---------------add multiple questions
		List<QuestionModel> savedQuestions=new ArrayList<>();
		for(QuestionModel q: questions) {
			try {
				if(questionRepo.existsByQuestion(q.getQuestion())) {
					continue;
				}else {
					savedQuestions.add(questionRepo.save(q));
				}
			}catch(Exception e){

			}
		}
		return savedQuestions;
	}

	public  Map<String, Object> verfiyAnswers(Map<String, String> userAnswers) {			//-------verify answers
		Map<String, Boolean> response= new HashMap<>();
		int correct=0;
		int wrong=0;
		int notAnswered=0;
		
		for(Map.Entry<String, String> entry: userAnswers.entrySet()) {
			
			Long qid=Long.parseLong(entry.getKey());
			String userAnswer=entry.getValue();
			
			QuestionModel questionModel= questionRepo.findById(qid).orElse(null);
			if(questionModel!=null) {
				boolean isCorrect= questionModel.getAnswer().equals(userAnswer);
				response.put(String.valueOf(qid), isCorrect);
				if(isCorrect) {
					correct++;
				}else {
					if(userAnswer.equals("")) {
						notAnswered++;
					}else {
						wrong++;
					}
				}
			}else {
				response.put(String.valueOf(qid), false);
				wrong++;
			}
		}
		//detail result
		Map<String,Object> detailResult= new HashMap<>();
		detailResult.put("responses", response);
		detailResult.put("correct",correct);
		detailResult.put("notAnswered", notAnswered);
		detailResult.put("wrong",wrong);
		
		return detailResult;
	}
}
