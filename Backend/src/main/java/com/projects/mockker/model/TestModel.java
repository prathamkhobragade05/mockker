package com.projects.mockker.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tests")
public class TestModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id", nullable = false)
    private Long id;

    @Column(name = "test_name", nullable = false)
    private String test;

    public TestModel(Long id, String test) {
        this.id = id;
        this.test = test;
    }

    public TestModel() {}

    public Long getId() {return id;}
    public String getTest() {return test;}

    public void setId(Long id) {this.id = id;}
    public void setTest(String test) {this.test = test;}
}
