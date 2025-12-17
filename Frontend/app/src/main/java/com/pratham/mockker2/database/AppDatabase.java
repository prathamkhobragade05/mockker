package com.pratham.mockker2.database;

import android.content.Context;
import android.security.identity.AuthenticationKeyMetadata;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.pratham.mockker2.database.DAO.*;

@Database(
        entities = {
                AllEntity.UserEntity.class,
                AllEntity.QuestionEntity.class,
                AllEntity.ResultEntity.class,
                AllEntity.TestEntity.class,
                AllEntity.TopicEntity.class
        },
        version = 2,
        exportSchema = false
)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract TestDao testDao();
    public abstract TopicDao topicDao();
    public abstract ResultDao resultDao();
    public abstract QuestionDao questionDao();


    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "mockker.db"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
