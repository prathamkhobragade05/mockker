package com.pratham.mockker2;

public class ModelTest {
    Long id;
    String test;

    public ModelTest(Long id,String test){
        this.id=id;
        this.test=test;
    }
    public ModelTest(){}

    public Long getId() {
        return id;
    }
    public String getTest() {
        return test;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setTest(String test) {
        this.test = test;
    }
}
