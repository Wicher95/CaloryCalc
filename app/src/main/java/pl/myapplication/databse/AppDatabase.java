package pl.myapplication.databse;

import android.content.Context;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import pl.myapplication.databse.dao.ProductDAO;
import pl.myapplication.databse.dao.UserDAO;
import pl.myapplication.databse.entities.Product;
import pl.myapplication.databse.entities.User;

@Database(version = 1, entities = {User.class, Product.class}, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    @VisibleForTesting
    public static final String DATABASE_NAME = "calory-calc-db";

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    private static AppDatabase sInstance;

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            sInstance = buildDatabase(context, executors);
        }
        return sInstance;
    }

    private static AppDatabase buildDatabase(final Context appContext, final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIO().execute(() -> {
                            // Generate the data
                            AppDatabase database = AppDatabase.getInstance(appContext, executors);
                            database.setDatabaseCreated();
                        });
                    }
                })
                .build();
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

    //DAO's
    public abstract UserDAO userDAO();

    public abstract ProductDAO productDao();
}
