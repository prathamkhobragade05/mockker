package com.projects.mockker.model;

import jakarta.persistence.*; 
import lombok.Data;

@Data
@Entity
@Table(name="Questions")
public class QuestionModel {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
	@Column(name="question_id")
	private Long id;
    
    @Column(name="test_id", nullable=false)
    private Long test;
    
    @Column(name="topic_id", nullable=false)
    private Long topic;
    
    @Column(name="direction" )
    private String direction;
    
	@Column(name="question" ,nullable = false, unique = true)
    private String question;
    
    @Column(name="options", nullable = false)
    private String[] options;

    @Column(name="answer", nullable = false)
    private String answer;
    


	
    public QuestionModel(Long id, Long test, Long topic,String direction, String question, String[] options, String answer) {
    	this.id=id;
    	this.test=test;
    	this.topic=topic;
    	this.direction=direction;
    	this.question=question;
    	this.options=options;
    	this.answer=answer;
    }
    
    public QuestionModel() {}

    public Long getId() {return id;}
    public String getDirection() {return direction;}
    public String getQuestion() {return question;}
    public String[] getOptions() {return options;}
    public String getAnswer() {return answer;}
    public Long getTopic() {return topic;}
    public Long getTest() {return test;}
    
    public void setId(Long id) { this.id=id;}
    public void setDirction(String direction) {this.direction=direction;}
    public void setQuestion(String question) {this.question=question;}
    public void setOptions(String[] options) {this.options=options;}
    public void setAnswer(String answer) {this.answer=answer;}
    public void setTopic(Long topic) {this.topic = topic;}
    public void setTest(Long test) {this.test = test;}
    
}
