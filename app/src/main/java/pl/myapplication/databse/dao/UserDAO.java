package pl.myapplication.databse.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import pl.myapplication.databse.entities.User;

@Dao
public interface UserDAO {
    @Insert
    void addUser(User user);

    @Update
    void updateUser(User User);

    @Delete
    void deleteUser(User User);

    @Query("SELECT * FROM users")
    List<User> retrieveUsers();

    @Query("SELECT * FROM users WHERE id=:userId")
    User getUserById(final int userId);
}
