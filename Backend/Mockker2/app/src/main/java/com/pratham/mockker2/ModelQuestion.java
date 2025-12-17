package com.pratham.mockker2;

public class ModelQuestion {
    Long id;
    Long test;
    Long topic;
    String direction;
    String question;
    String[] options;
    String answer;
    String selected = "";

    public ModelQuestion(Long id, Long test, Long topic,String direction, String question, String[] options, String answer) {
        this.id=id;
        this.test=test;
        this.topic=topic;
        this.direction=direction;
        this.question = question;
        this.options = options;
        this.answer = answer;
    }

    public ModelQuestion(){}

    public Long getId(){return id;}
    public Long getTest() {return test;}
    public Long getTopic() {return topic;}
    public String getDirection(){return direction;}
    public String getQuestion() { return question; }
    public String[] getOptions() { return options; }
    public String getAnswer() { return answer; }
    public String getSelected() { return selected; }
    public void setSelected(String selected) { this.selected = selected; }
}
