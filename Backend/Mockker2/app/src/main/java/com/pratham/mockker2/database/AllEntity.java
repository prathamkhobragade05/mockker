package com.pratham.mockker2.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

public class AllEntity {

    @Entity(tableName = "User")
    public static class UserEntity{
        @PrimaryKey
        public long userId;
        public String userName;
    }

    @Entity(tableName ="Question_Bank")
    public static class QuestionEntity{
        @PrimaryKey
        public long questionId;
        public long testId;
        public long topicId;
        public String direction;
        public String question;
        public String[] options;
        public String answer;
    }

    @Entity(tableName = "Results")
    public static class ResultEntity {
        @PrimaryKey(autoGenerate = true)
        public long resultId;
        public long userId;
        public long testId;
        public long topicId;
        public String score;
        public String date;
    }

    @Entity(tableName = "Tests")
    public static class TestEntity{
        @PrimaryKey
        public long testId;
        public String testName;
    }
    @Entity(tableName = "Topics")
    public static class TopicEntity{
        @PrimaryKey
        public long topicId;
        public long testId;
        public String topicName;
        public Integer queCount;
    }

}
