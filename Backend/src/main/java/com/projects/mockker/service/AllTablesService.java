package com.projects.mockker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.projects.mockker.model.AllTables;
import com.projects.mockker.repository.*;

@Service
public class AllTablesService {

    @Autowired private QuestionRepository questionRepository;
    @Autowired private ResultRepository resultRepository;
    @Autowired private TestRepository testRepository;
    @Autowired private TestTopicRepository testTopicRepository;
//    @Autowired private UserRepository userRepository;

    public AllTables getAllTables(Long userId) {
        AllTables all = new AllTables();
        
        all.setTests(testRepository.findAll());
        all.setTopics(testTopicRepository.findAll());
        all.setQuestions(questionRepository.findAll());
        all.setResults(resultRepository.findByUserIdOrderByDateTimeDesc(userId));
//        all.setUsers(userRepository.findAll());

        return all;
    }
}
