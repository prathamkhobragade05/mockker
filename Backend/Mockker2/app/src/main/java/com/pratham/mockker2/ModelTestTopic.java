package com.pratham.mockker2;

public class ModelTestTopic {
    Long id;
    Long test;
    String topic;

    public ModelTestTopic(Long id,Long test, String topic) {
        this.id = id;
        this.test=test;
        this.topic = topic;
    }

    public ModelTestTopic() {}

    public Long getId() {return id;}
    public Long getTest() {return test;}
    public String getTopic() {return topic;}

    public void setId(Long id) {this.id = id;}
    public void setTest(Long test) {this.test= test;}
    public void setTopic(String topic) {this.topic = topic;}


}
