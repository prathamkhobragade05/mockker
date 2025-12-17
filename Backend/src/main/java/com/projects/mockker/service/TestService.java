package com.projects.mockker.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mockker.model.TestModel;
import com.projects.mockker.model.TestTopicModel;
import com.projects.mockker.repository.TestRepository;
import com.projects.mockker.repository.TestTopicRepository;

@Service
public class TestService {
	@Autowired
	TestRepository testRepo;
	
	@Autowired 
	TestTopicRepository topicRepo;

	public List<TestModel> getAlltests() {								//---------------get all tests
		return testRepo.findAll();
	}
	
	public TestModel addTest(TestModel test) {								//---------------add test
        return testRepo.save(test);
    }

	public List<TestModel> addTests(List<TestModel> tests) {								//---------------add multiple tests
		return testRepo.saveAll(tests);
	}

	public List<TestTopicModel> getTopicsByTest(Long test) {								//---------------get topics by test
		return topicRepo.findByTest(test);
	}

	public TestTopicModel addTopic(TestTopicModel topic) {								//---------------add topic by test
		return  topicRepo.save(topic);
	}
	
	public List<TestTopicModel> addTopicsByTest(List<TestTopicModel> topics) {				//-------------- add multiple topics by test
		List<TestTopicModel> savedTopics=new ArrayList<>();
		for(TestTopicModel topic: topics) {
			try {
				if(topicRepo.existsByTopic(topic.getTopic())) {
					continue;
				}else {
					savedTopics.add(topicRepo.save(topic));
				}
				
			}catch(Exception e) {
				
			}
		}
		return savedTopics;
	}

}
