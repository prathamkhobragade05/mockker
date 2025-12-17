package com.projects.mockker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.mockker.model.TestModel;

@Repository
public interface TestRepository extends JpaRepository<TestModel,Long>{

}
