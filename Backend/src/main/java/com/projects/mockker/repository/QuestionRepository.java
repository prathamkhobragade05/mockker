package com.projects.mockker.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import com.projects.mockker.model.QuestionModel;
@EnableJpaRepositories
@Repository
public interface QuestionRepository extends JpaRepository<QuestionModel,Long>{

	boolean existsByQuestion(String question);

	List<QuestionModel> findByTestAndTopic(Long test,Long topic);

}
