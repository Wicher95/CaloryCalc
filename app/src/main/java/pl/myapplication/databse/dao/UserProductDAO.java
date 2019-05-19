package pl.myapplication.databse.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.myapplication.databse.entities.UserProduct;

@Dao
public interface UserProductDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addUserProduct(UserProduct userProduct);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void addUserProducts(List<UserProduct> userProducts);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUserProduct(UserProduct userProduct);

    @Delete
    void deleteUserProduct(UserProduct userProduct);

    @Query("SELECT * FROM userProducts")
    List<UserProduct> getAllUsersProducts();

    @Query("SELECT * FROM userProducts WHERE userId=:userId")
    List<UserProduct> getUserProducts(final int userId);
}
