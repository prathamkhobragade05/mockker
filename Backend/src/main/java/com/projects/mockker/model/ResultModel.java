package com.projects.mockker.model;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name="Results")
public class ResultModel {
	@Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="result_id" )
    private Long id;

	@Column(name="user_id")
    private Long userId;
	
    @Column(name="test_id")
    private Long testId;
    
    @Column(name="topic_id")
    private Long topicId;
    
    @Column(name="score")
    private String score;
    @Column(name="date_time")
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime  dateTime;

    public ResultModel(Long id,Long userId, Long testId,Long topicId, String score,LocalDateTime dateTime){
        this.id=id;
        this.userId=userId;
        this.testId=testId;
        this.topicId=topicId;
        this.score=score;
        this.dateTime=dateTime;
    }
    public ResultModel(){}

    public Long getId() {return id;}
    public Long getUserId() {return userId;}
    public Long getTestId() {return testId;}
    public Long getTopicId() {return topicId;}
    public String getScore() {return score;}
    public LocalDateTime getDateTime() {return dateTime;}

    public void setId(Long id) {this.id = id;}
    public void setUserId(Long userId) {this.userId = userId;}
    public void setTestId(Long testId) {this.testId = testId;}
    public void setTopicid(Long topicId) {this.topicId= topicId;}
    public void setScore(String score) {this.score = score;}
    public void setDateTime(LocalDateTime dateTime) {this.dateTime = dateTime;}


}
