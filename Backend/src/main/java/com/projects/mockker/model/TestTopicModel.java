package com.projects.mockker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "test_topics")
public class TestTopicModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Long id;
    
    @Column(name="test_id",nullable=false)
    private Long test;

    @Column(name = "topic_name", nullable = false)
    private String topic;

    public TestTopicModel(Long id,Long test, String topic) {
        this.id = id;
        this.test=test;
        this.topic = topic;
    }

    public TestTopicModel() {}

    public Long getId() {return id;}
    public Long getTest() {return test;}
    public String getTopic() {return topic;}

    public void setId(Long id) {this.id = id;}
    public void setTest(Long test) {this.test= test;}
    public void setTopic(String topic) {this.topic = topic;}

}
