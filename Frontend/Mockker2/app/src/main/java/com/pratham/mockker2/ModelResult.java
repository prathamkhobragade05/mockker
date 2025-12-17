package com.pratham.mockker2;

public class ModelResult {
    Long id;
    Long userId;
    Long topicId;
    Long testId;
    String  set;
    String score;
    String dateTime;

    public ModelResult(Long id, Long userId, Long testId,Long topicId, String score, String dateTime){
        this.id=id;
        this.userId=userId;
        this.testId=testId;
        this.topicId=topicId;
        this.score=score;
        this.dateTime=dateTime;
    }
    public ModelResult(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTestId() {
        return testId;
    }

    public void setTestId(Long testId) {
        this.testId = testId;
    }

    public Long getTopicId(){return topicId;}

    public void setTopicId(Long topicId){this.topicId=topicId;}

    public String getSet(){return set;}

    public void setSet(String set){this.set=set; }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDateTime() {return dateTime;}

    public void setDateTime(String dateTime) {this.dateTime = dateTime;}
}
