package com.projects.mockker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.mockker.model.TestTopicModel;

@Repository
public interface TestTopicRepository extends JpaRepository<TestTopicModel, Long>{

	List<TestTopicModel> findByTest(Long test);

	boolean existsByTopic(String topic);
	
}
