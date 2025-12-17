package com.projects.mockker.model;

import java.util.List;
import lombok.Data;

@Data
public class AllTables {
    private List<QuestionModel> questions;
    private List<ResultModel> results;
    private List<TestModel> tests;
    private List<TestTopicModel> topics;
    private List<UserModel> users;
	public List<QuestionModel> getQuestions() {
		return questions;
	}
	public void setQuestions(List<QuestionModel> questions) {
		this.questions = questions;
	}
	public List<ResultModel> getResults() {
		return results;
	}
	public void setResults(List<ResultModel> results) {
		this.results = results;
	}
	public List<TestModel> getTests() {
		return tests;
	}
	public void setTests(List<TestModel> tests) {
		this.tests = tests;
	}
	public List<TestTopicModel> getTopics() {
		return topics;
	}
	public void setTopics(List<TestTopicModel> topics) {
		this.topics = topics;
	}
	public List<UserModel> getUsers() {
		return users;
	}
	public void setUsers(List<UserModel> users) {
		this.users = users;
	}
}
