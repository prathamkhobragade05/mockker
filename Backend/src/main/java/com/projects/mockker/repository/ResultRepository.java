package com.projects.mockker.repository;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projects.mockker.model.ResultModel;

@Repository
public interface ResultRepository extends JpaRepository<ResultModel, Long> {
    List<ResultModel> findByUserIdOrderByDateTimeDesc(Long userId);
    
    boolean existsByUserIdAndDateTime(Long userId,LocalDateTime dateTime);

	List<ResultModel> findAllByUserId(Long userId);

	void deleteAllByUserId(Long userId);


}
