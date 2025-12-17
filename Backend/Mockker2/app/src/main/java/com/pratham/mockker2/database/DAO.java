package com.pratham.mockker2.database;


import com.pratham.mockker2.ModelQuestion;
import com.pratham.mockker2.database.AllEntity.*;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public class DAO {


    @Dao
    public interface UserDao{
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertUser(UserEntity user);

        @Query("select userId from user")
        Long getLoginId();

        @Query("select userName from user")
        String getUserName();

        @Query("delete from user")
        void deleteUserId();
    }
    @Dao
    public interface TestDao{
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertAllTests(List<TestEntity> testList);

        @Query("DELETE FROM tests WHERE testId NOT IN (:testIds)")
        int deleteExceptTestId(List<Long> testIds);

        @Query("select * from tests")
        List<TestEntity> getAllTests();
    }

    @Dao
    public interface TopicDao{
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        void insertAllTopics(List<TopicEntity> topicList);

        @Query("DELETE FROM topics WHERE topicId NOT IN (:topicIds)")
        int deleteExceptTopicId(List<Long> topicIds);

        @Query("select * from topics ")
        List<TopicEntity> getAllTopics();

        @Query("select * from topics where testId=:testId")
        List<TopicEntity> getTopicsByTestId(Long testId);

        @Query("select  queCount from topics where topicId=:topicId")
        int getTopicQueCount(Long topicId);

        @Query("update topics set queCount=:queCount where topicId=:topicId")
        void updateQueCount(Integer queCount,Long topicId);
    }

    @Dao
    public interface QuestionDao{
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertAllQuestions(List<AllEntity.QuestionEntity> questionList);

//        @Query("select * from question_bank")
//        List<ModelQuestion> getAllQuestions();                          //---------QuestionEntity karaych ahe nntr ModelQuestions

        @Query("select count(*) from question_bank")
        int getAllQuestionCount();

        @Query("DELETE FROM question_bank WHERE questionId NOT IN (:ids)")
        void deleteExceptQuestionId(List<Long> ids);

        @Query("select count(*) from question_bank where topicId=:topicId")
        int getQuestionCountByTopicId(Long topicId);

        @Query("select * from question_bank where topicId=:topicId")
        List<QuestionEntity> getQuestionsByTopicId(Long topicId);
    }


    @Dao
    public interface ResultDao{
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertAll(List<ResultEntity> resultList);

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        void insertResult(ResultEntity resultList);

        @Query("delete from results where resultId=:resultId")
        int deleteResult(Long resultId);

        @Query("select count(*) from results order by date desc")
        int getAllResultsCount();

        @Query("select * from results order by date desc")
        List<ResultEntity> getAllResults();

        @Query("delete from results")
        void deleteAllResults();

        @Query("DELETE FROM sqlite_sequence WHERE name = 'results'")
        void resetResultId();

        @Transaction
        default void replaceAll(List<ResultEntity> list) {
            deleteAllResults();
            resetResultId();
            insertAll(list);
        }
    }
}
