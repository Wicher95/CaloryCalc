package pl.myapplication.application;

import android.app.Application;

import pl.myapplication.databse.AppDatabase;
import pl.myapplication.databse.AppExecutors;

public class CaloryCalc extends Application {
    private static Application instance;
    private static AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mAppExecutors = new AppExecutors();
        getDatabase();
    }

    public static AppDatabase getDatabase() {
        return AppDatabase.getInstance(instance, mAppExecutors);
    }

    public static Application getInstance() {
        return instance;
    }
}
